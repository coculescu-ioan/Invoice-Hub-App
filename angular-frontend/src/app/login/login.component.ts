import { Component } from '@angular/core';
import { AuthService } from '../auth.service';
import { LoginRequest } from '../auth-requests';
import { Router } from '@angular/router';
import { UserService } from '../user.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginRequest: LoginRequest = { username: '', password: '' };
  errorMessages: string[] = [];

  constructor(
    private authService: AuthService, 
    private router: Router,
    private userService: UserService
  ) {}

  login() {
    this.authService.login(this.loginRequest).subscribe({
      next: (response) => {
        // Handle successful login and navigate to dashboard
        this.userService.setUsername(this.loginRequest.username);
        this.router.navigate(['/dashboard']);
      },
      error: (error: HttpErrorResponse) => {
        // Handle login error (DOES NOT WORK)
        if (error.status === 401 && error.error instanceof Array) {
          this.errorMessages = error.error;
        } else {
          this.errorMessages = ['Failed to login'];
        }
      }
    });
  }
}
