/**
 * Angular Routing Module
 * This module defines the routes for the application.
 */
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { authGuard } from './guard/auth.guard';
import { CallbackComponent } from './components/callback/callback.component';
import {RegisterComponent} from './components/register/register.component';

/**
 * Defines the routes for the application.
 * - `home`: Protected route, accessible only to authenticated users.
 * - `login`: Public route for user login.
 * - `callback`: Route for handling authentication callbacks.
 * - `register`: Public route for user registration.
 */
const routes: Routes = [
  {path: 'home', component: HomeComponent, canActivate:[authGuard]},
  {path: 'login', component:LoginComponent},
  {path: '', redirectTo:'home' , pathMatch:'full'},
  {path: 'callback', component: CallbackComponent },
  {path: 'register', component: RegisterComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
