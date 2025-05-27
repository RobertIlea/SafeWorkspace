import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CustomAlertService } from '../../services/custom-alert.service';
import { CustomAlert } from '../../models/customAlert-model';

@Component({
  selector: 'app-edit-custom-alert-dialog',
  templateUrl: './edit-custom-alert-dialog.component.html',
  standalone: false,
  styleUrls: ['./edit-custom-alert-dialog.component.css']
})
export class EditCustomAlertDialogComponent implements OnInit {
  form: FormGroup;
  availableParams: string[] = [];

  constructor(
    private fb: FormBuilder,
    private customAlertService: CustomAlertService,
    private dialogRef: MatDialogRef<EditCustomAlertDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: CustomAlert
  ) {
    this.availableParams = [data.parameter ?? ''];
    this.form = this.fb.group({
      parameter: [data.parameter, Validators.required],
      condition: [data.condition, Validators.required],
      threshold: [data.threshold, [Validators.required]],
      message: [data.message, Validators.required],
    });
  }

  ngOnInit(): void {}

  submit() {
    const updatedAlert: CustomAlert = {
      ...this.data,
      ...this.form.value
    };
    console.log("id: ", this.data.id);
    this.customAlertService.update_custom_alert(this.data.id!, updatedAlert).subscribe({
      next: () => this.dialogRef.close(true),
      error: (err) => console.error('Error updating alert:', err)
    });
  }
}
