import { Component } from '@angular/core';
import { AuthService } from '../auth.service';
import { ForgetPasswordRequest } from '../auth-requests';

@Component({
  selector: 'app-forget-password',
  templateUrl: './forget-password.component.html',
  styleUrls: ['./forget-password.component.css']
})
export class ForgetPasswordComponent {
  forgetPasswordRequest: ForgetPasswordRequest = { email: '' };
  message: string = '';

  constructor(private authService: AuthService) {}

  forgetPassword() {
    this.authService.forgetPassword(this.forgetPasswordRequest).subscribe(
      response => {
        // Handle successful password reset request
        this.message = 'Password reset instructions sent to your email';
      },
      error => {
        // Handle error
        this.message = 'Failed to send password reset instructions';
      }
    );
  }
}
