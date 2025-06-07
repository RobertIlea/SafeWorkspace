/**
 * app.module.ts
 * This is the main module of the Angular application.
 * It imports necessary Angular modules.
 * It declares components used in the application.
 */
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './components/home/home.component';
import { MatIconModule } from '@angular/material/icon';
import {MatTabsModule} from '@angular/material/tabs';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import {MatListModule} from '@angular/material/list';
import { MatButtonModule } from '@angular/material/button';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatCardModule } from '@angular/material/card';
import { MatMenuModule } from '@angular/material/menu';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import {MatSelectModule} from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { MatInputModule } from '@angular/material/input';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatDialogModule} from '@angular/material/dialog';
import { HTTP_INTERCEPTORS, provideHttpClient, withFetch } from '@angular/common/http';
import { RegisterComponent } from './components/register/register.component';
import { LoginComponent } from './components/login/login.component';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import { CallbackComponent } from './components/callback/callback.component';
import { RoomDialogComponent } from './components/room-dialog/room-dialog.component';
import { AddRoomComponent } from './components/add-room/add-room.component';
import { BaseChartDirective } from 'ng2-charts';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { AlertComponent } from './components/alert/alert.component';
import { AlertDialogComponent } from './components/alert-dialog/alert-dialog.component';
import { ConfirmDialogComponent } from './components/confirm-dialog/confirm-dialog.component';
import { CustomAlertDialogComponent } from './components/custom-alert-dialog/custom-alert-dialog.component';
import { CustomAlertManagerComponent } from './components/custom-alert-manager/custom-alert-manager.component';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell, MatHeaderCellDef,
  MatHeaderRow, MatHeaderRowDef,
  MatRow, MatRowDef,
  MatTable
} from '@angular/material/table';
import { EditCustomAlertDialogComponent } from './components/edit-custom-alert-dialog/edit-custom-alert-dialog.component';
@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    SidebarComponent,
    DashboardComponent,
    RegisterComponent,
    LoginComponent,
    CallbackComponent,
    RoomDialogComponent,
    AddRoomComponent,
    AlertComponent,
    AlertDialogComponent,
    ConfirmDialogComponent,
    CustomAlertDialogComponent,
    CustomAlertManagerComponent,
    EditCustomAlertDialogComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    MatIconModule,
    MatTabsModule,
    MatButtonModule,
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    MatGridListModule,
    MatCardModule,
    MatMenuModule,
    MatFormFieldModule,
    MatSelectModule,
    MatOptionModule,
    MatInputModule,
    FormsModule,
    MatDialogModule,
    MatSnackBarModule,
    BaseChartDirective,
    MatDatepickerModule,
    MatNativeDateModule,
    ReactiveFormsModule,
    MatTable,
    MatHeaderRow,
    MatRow,
    MatHeaderCell,
    MatColumnDef,
    MatCell,
    MatCellDef,
    MatHeaderCellDef,
    MatHeaderRowDef,
    MatRowDef
  ],
  providers: [
    provideHttpClient(withFetch()),
    MatDatepickerModule,
    MatNativeDateModule
  ],
  bootstrap: [AppComponent],
})
export class AppModule { }
