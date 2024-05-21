import { Component } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { ForgetPasswordRequest } from '../auth/auth-requests';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';

@Component({
  selector: 'app-forget-password',
  templateUrl: './forget-password.component.html',
  styleUrls: ['./forget-password.component.css']
})
export class ForgetPasswordComponent {
  forgetPasswordRequest: ForgetPasswordRequest = { email: '' };
  message: string = '';
  errorMessages: string[] = [];

  constructor(private authService: AuthService) {}

  forgetPassword() {
    this.authService.forgetPassword(this.forgetPasswordRequest).subscribe({
      next: (response: HttpResponse<any>) => {
        // Handle successful password reset request
        this.message = 'Password reset instructions sent to your email';
      },
      error: (error: HttpErrorResponse) => {
        if (error.status === 400 && error.error instanceof Array) {
          this.errorMessages = error.error;
        } else {
          this.errorMessages = ['Failed to send password reset instructions'];
        }
      }
    });
  }
}
