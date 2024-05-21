import { Component } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { LoginRequest } from '../auth/auth-requests';
import { Router } from '@angular/router';
import { UserService } from '../user.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginRequest: LoginRequest = { username: '', password: '' };
  errorMessages: string[] = [];
  token: string | null = null;

  constructor(
    private authService: AuthService, 
    private router: Router,
    private userService: UserService
  ) {}

  login() {
    this.authService.login(this.loginRequest).subscribe({
      next: (response: HttpResponse<any>) => {
        this.token = this.authService.getToken();
        if (this.token) {
          console.log('Token:', this.token);  // Debugging line
          this.userService.setUsername(this.loginRequest.username);
          this.router.navigate(['/dashboard']);
        } else {
          this.errorMessages = ['Failed to login'];
        }
      },
      error: (error: HttpErrorResponse) => {
        console.log(this.errorMessages); // Debugging line
        if (error.status === 401 && error.error instanceof Array) {
          this.errorMessages = error.error;
        } else {
          this.errorMessages = ['Failed to login'];
        }
      }
    });
  }
}
