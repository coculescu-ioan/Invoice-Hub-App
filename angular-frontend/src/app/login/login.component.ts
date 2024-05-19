import { Component } from '@angular/core';
import { AuthService } from '../auth.service';
import { LoginRequest } from '../auth-requests';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginRequest: LoginRequest = { username: '', password: '' };
  errorMessage: string = '';

  constructor(private authService: AuthService, private router: Router) {}

  login() {
    this.authService.login(this.loginRequest).subscribe(
      response => {
        // Handle successful login, maybe navigate to a different page
        this.router.navigate(['/']);
      },
      error => {
        // Handle login error
        this.errorMessage = 'Invalid username or password';
      }
    );
  }
}
