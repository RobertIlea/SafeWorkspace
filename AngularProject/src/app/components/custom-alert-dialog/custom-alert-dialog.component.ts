import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {CustomAlertService} from '../../services/custom-alert.service';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {AlertEventsService} from '../../services/alert-events.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {User} from '../../models/user-model';
import {UserService} from '../../services/user.service';

@Component({
  selector: 'app-custom-alert-dialog',
  standalone: false,

  templateUrl: './custom-alert-dialog.component.html',
  styleUrl: './custom-alert-dialog.component.css'
})
export class CustomAlertDialogComponent implements OnInit {
  alertForm: FormGroup;
  currentMin: number = 0;
  currentMax: number = 0;
  availableParams: string[] = [];
  currentUser: User | null;

  paramThresholdLimits: { [key: string]: { min: number; max: number } } = {
    temperature: { min: 0, max: 49.9 },
    humidity: { min: 0, max: 90 },
    gas: { min: 100, max: 699 },
    mq2Value: { min: 0, max: 799 }
  };

  sensorTypeToParamMap: { [sensorType: string]: string[] } = {
    DHT22: ['temperature', 'humidity'],
    MQ2: ['mq2Value'],
    MQ5: ['gas']
  };

  countryPrefixes = [
    { label: 'Romania', value: '+40' },
    { label: 'United States', value: '+1' },
    { label: 'United Kingdom', value: '+44' },
    { label: 'Germany', value: '+49' },
    { label: 'France', value: '+33' }
  ]

  constructor(private fb: FormBuilder,private snackBar: MatSnackBar ,private customAlertService: CustomAlertService, private alertEventsService: AlertEventsService, private dialogRef: MatDialogRef<CustomAlertDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any, private userService: UserService) {
    const rawParams = data?.sensor?.details?.[0]?.data;
    const sensorType = data?.sensor?.sensorType;

    this.availableParams = rawParams ? Object.keys(rawParams) : this.sensorTypeToParamMap[sensorType] || [];

    this.alertForm = this.fb.group({
      parameter: [this.availableParams[0], Validators.required],
      condition: ['>', Validators.required],
      threshold: [null, [Validators.required, Validators.min(0)]],
      message: ['', Validators.required]
    });

    const initialLimits = this.paramThresholdLimits[this.availableParams[0]];
    if (initialLimits) {
      this.currentMin = initialLimits.min;
      this.currentMax = initialLimits.max;
    }

    this.currentUser = data?.user || null;
  }


  ngOnInit(): void {
    this.validateThresholdLimit();

    this.userService.get_user_phone_by_id(this.data.userId).subscribe({
      next: (phone: string) => {
        if (!phone) {
          this.addPhoneControls();
        }
      },
      error: (error) => {
        if( error.status === 500 || error.status === 404) {
          this.addPhoneControls();
        }
      }
    });
  }

  addPhoneControls(): void {
    this.alertForm.addControl('countryPrefix', this.fb.control('+40', Validators.required));
    this.alertForm.addControl('phone', this.fb.control('', [
      Validators.required,
      Validators.pattern(/^[0-9]{7,15}$/)
    ]));
  }


  submit(){
    if (this.alertForm) {
      const alert = {
        userId: this.data.userId,
        roomId: this.data.room.id,
        sensorId: this.data.sensor.id,
        sensorType: this.data.sensor.sensorType,
        ...this.alertForm.value,
      };

      if (this.alertForm.contains('phone')) {
        const phone = this.alertForm.get('countryPrefix')?.value + this.alertForm.get('phone')?.value;
        this.userService.update_user_phone(this.data.userId, phone).subscribe();
      }



      this.customAlertService.create_custom_alert(alert).subscribe({
        next: () => {
          this.alertEventsService.customAlertCreated$.next();
          this.snackBar.open('Custom alert created successfully!', 'Close', {
            duration: 3000,
          });
          this.dialogRef.close(true);
        },
        error: () => {
          this.snackBar.open('Failed to create custom alert. Please try again.', 'Close', {
            duration: 3000,
          });
        }
      });
    } else {
      this.snackBar.open('Form is invalid. Please check your inputs.', 'Close', {
        duration: 3000,
      });
    }
  }

  close() {
    this.dialogRef.close();
  }

  validateThresholdLimit() {
    this.alertForm.get('parameter')?.valueChanges.subscribe((param: string) => {
      const thresholdControl = this.alertForm.get('threshold');
      const limits = this.paramThresholdLimits[param];

      thresholdControl?.setValidators([
        Validators.required,
        Validators.min(limits.min),
        Validators.max(limits.max)
      ]);

      thresholdControl?.updateValueAndValidity();

      this.currentMin = limits.min;
      this.currentMax = limits.max;
    });
  }

}
