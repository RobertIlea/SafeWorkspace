import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditCustomAlertDialogComponent } from './edit-custom-alert-dialog.component';

describe('EditCustomAlertDialogComponent', () => {
  let component: EditCustomAlertDialogComponent;
  let fixture: ComponentFixture<EditCustomAlertDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditCustomAlertDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditCustomAlertDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
