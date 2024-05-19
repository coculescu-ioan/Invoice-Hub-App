import { Component } from '@angular/core';
import { AuthService } from '../auth.service';
import { SignupRequest } from '../auth-requests';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  signupRequest: SignupRequest = { username: '', password: '', email: '' };
  errorMessage: string = '';

  constructor(private authService: AuthService, private router: Router) {}

  register() {
    this.authService.register(this.signupRequest).subscribe(
      response => {
        // Handle successful registration, maybe navigate to login page
        this.router.navigate(['/login']);
      },
      error => {
        // Handle registration error
        this.errorMessage = 'Failed to register';
      }
    );
  }
}
