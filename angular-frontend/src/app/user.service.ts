import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly USERNAME_KEY = 'username';

  constructor() {
    // Retrieve the username from session storage when the service is instantiated
    this.username = sessionStorage.getItem(this.USERNAME_KEY) || '';
  }

  private username: string = '';

  setUsername(username: string) {
    this.username = username;
    // Store the username in session storage
    sessionStorage.setItem(this.USERNAME_KEY, username);
  }

  getUsername(): string {
    return this.username;
  }
}
