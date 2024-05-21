import { Component } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { SignupRequest } from '../auth/auth-requests';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  signupRequest: SignupRequest = { username: '', password: '', email: '' };
  errorMessages: string[] = [];

  constructor(private authService: AuthService, private router: Router) {}

  register() {
    this.authService.register(this.signupRequest).subscribe({
      next: (response) => {
        // Handle successful registration, maybe navigate to login page
        this.router.navigate(['/login']);
      },
      error: (error: HttpErrorResponse) => {
        // Handle registration error
        if (error.status === 400 && error.error instanceof Array) {
          this.errorMessages = error.error;
        } else {
          this.errorMessages = ['Failed to register'];
        }
      }
    });
  }
}
