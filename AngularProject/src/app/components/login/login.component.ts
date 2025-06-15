import { Component } from '@angular/core';
import { User } from '../../models/user-model';
import { LoginService } from '../../services/login.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: false,

  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  email:string = '';
  password: string = '';
  user: User | undefined;

  constructor(private loginService: LoginService, private router: Router){}

  onLogin():void{
    this.loginService.postUser(this.email,this.password).subscribe({
      next: (res) => {
        const token = res.token;
        console.log("token received...");
        this.user = res.user;

        if(token){
          localStorage.setItem('jwtToken', token);
          sessionStorage.setItem('user', JSON.stringify({user_data: this.user}));
          console.log("Token saved!");
          this.router.navigate(['/home']);
        }else{
          console.error("No token received!!");
        }
      },
      error: (err) =>{
        console.log("Login failed: ", err);
      }
    })
  }

  loginWithGoogle():void{
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
  }
}
