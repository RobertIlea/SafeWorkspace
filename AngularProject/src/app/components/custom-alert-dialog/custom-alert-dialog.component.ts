import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {CustomAlertService} from '../../services/custom-alert.service';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';

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
  availableParams: string[] = []
  paramThresholdLimits: { [key: string]: { min: number; max: number } } = {
    temperature: { min: 0, max: 39.9 },
    humidity: { min: 0, max: 90 },
    gas: { min: 100, max: 699 },
    mq2Value: { min: 0, max: 1000 }
  };


  ngOnInit(): void {
    this.validateThresholdLimit();
  }
  constructor(private fb: FormBuilder, private customAlertService: CustomAlertService, private dialogRef: MatDialogRef<CustomAlertDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any) {
    this.availableParams = Object.keys(data?.sensor?.details?.[0]?.data || {});

    this.alertForm = this.fb.group({
      parameter: [this.availableParams[0], Validators.required],
      condition: ['>', Validators.required],
      threshold: [null, [Validators.required, Validators.min(0)]],
      message: ['', Validators.required],
    });

    const initialParam = this.alertForm.get('parameter')?.value;
    const initialLimits = this.paramThresholdLimits[initialParam];
    this.currentMin = initialLimits.min;
    this.currentMax = initialLimits.max;
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

      this.customAlertService.create_custom_alert(alert).subscribe({
        next: (response) => {
          console.log('Custom alert created:', response);
          this.dialogRef.close(true);
        },
        error: (error) => {
          console.error('Error creating custom alert:', error);
        }
      });
    } else {
      console.error('Form is invalid');
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
