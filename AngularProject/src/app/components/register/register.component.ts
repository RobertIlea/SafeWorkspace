import {Component, OnInit} from '@angular/core';
import {User} from '../../models/user-model';
import {LoginService} from '../../services/login.service';
import {Router} from '@angular/router';
import {MatSnackBar} from '@angular/material/snack-bar';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-register',
  standalone: false,

  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup;
  user: User | undefined;

  constructor(private loginService: LoginService, private router: Router, private snackBar: MatSnackBar, private fb: FormBuilder) { }

  ngOnInit() {
    this.registerForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(5)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]]
    }, { validator: this.passwordsMatchValidator });
  }

  passwordsMatchValidator(group: FormGroup) {
    const pass = group.get('password')?.value;
    const confirm = group.get('confirmPassword')?.value;
    return pass === confirm ? null : { notMatching: true };
  }

  onRegister(): void{
    if (this.registerForm.invalid) {
      this.snackBar.open("Please fix the errors in the form.", "Close", {
        duration: 3000,
        panelClass: ['error-snackbar']
      });
      return;
    }

    const { name, email, password } = this.registerForm.value;

    this.loginService.registerUser(email, password, name).subscribe({
      next: (res) => {
        this.user = res.user;
        localStorage.setItem('jwtToken', res.token);
        sessionStorage.setItem('user', JSON.stringify({ user_data: this.user }));
        this.router.navigate(['/home']);
        this.snackBar.open("Registration successful!", "Close", {
          duration: 3000,
          panelClass: ['success-snackbar']
        });
      },
      error: () => {
        this.snackBar.open("Registration failed. Please try again.", "Close", {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }
}
