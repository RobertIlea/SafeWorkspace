import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomAlertManagerComponent } from './custom-alert-manager.component';

describe('CustomAlertManagerComponent', () => {
  let component: CustomAlertManagerComponent;
  let fixture: ComponentFixture<CustomAlertManagerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CustomAlertManagerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CustomAlertManagerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
