import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { authGuard } from './guard/auth.guard';
import { CallbackComponent } from './components/callback/callback.component';

const routes: Routes = [
  {path: 'home', component: HomeComponent, canActivate:[authGuard]},
  {path: 'login', component:LoginComponent},
  {path: '', redirectTo:'home' , pathMatch:'full'},
  { path: 'callback', component: CallbackComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
