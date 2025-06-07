import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CustomAlertService } from '../../services/custom-alert.service';
import { CustomAlert } from '../../models/customAlert-model';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-edit-custom-alert-dialog',
  templateUrl: './edit-custom-alert-dialog.component.html',
  standalone: false,
  styleUrls: ['./edit-custom-alert-dialog.component.css']
})
export class EditCustomAlertDialogComponent implements OnInit {
  form: FormGroup;
  availableParams: string[] = [];
  currentMin: number = 0;
  currentMax: number = 0;
  paramThresholdLimits: { [key: string]: { min: number; max: number } } = {
    temperature: { min: 0, max: 49.9 },
    humidity: { min: 0, max: 90 },
    gas: { min: 100, max: 699 },
    mq2Value: { min: 0, max: 799 }
  };

  constructor(
    private fb: FormBuilder,
    private customAlertService: CustomAlertService,
    private dialogRef: MatDialogRef<EditCustomAlertDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: CustomAlert,
    private snackBar: MatSnackBar
  ) {
    this.availableParams = [data.parameter ?? ''];
    this.form = this.fb.group({
      parameter: [data.parameter, Validators.required],
      condition: [data.condition, Validators.required],
      threshold: [data.threshold, [Validators.required]],
      message: [data.message, Validators.required],
    });
  }

  ngOnInit(): void {
    const initialParam = this.form.get('parameter')?.value;
    const initialLimits = this.paramThresholdLimits[initialParam];

    this.currentMin = initialLimits.min;
    this.currentMax = initialLimits.max;

    const thresholdControl = this.form.get('threshold');
    thresholdControl?.setValidators([
      Validators.required,
      Validators.min(this.currentMin),
      Validators.max(this.currentMax)
    ]);
    thresholdControl?.updateValueAndValidity();

    this.validateThresholdLimit();
  }

  submit() {
    const updatedAlert: CustomAlert = {
      ...this.data,
      ...this.form.value
    };
    this.customAlertService.update_custom_alert(this.data.id!, updatedAlert).subscribe({
      next: () => this.dialogRef.close(true),
      complete: () => {
        this.snackBar.open('Alert updated successfully!', 'Close', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });
      },
      error: () => {
        this.snackBar.open('Failed to update alert. Please try again.', 'Close', {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }
  validateThresholdLimit() {
    this.form.get('parameter')?.valueChanges.subscribe((param: string) => {
      const thresholdControl = this.form.get('threshold');
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
