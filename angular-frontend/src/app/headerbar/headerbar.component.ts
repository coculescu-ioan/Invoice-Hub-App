import {Component, OnInit} from '@angular/core';
import { Router } from '@angular/router';
import {UserService} from "../services/user.service";
// Import authentication service if available

@Component({
  selector: 'app-headerbar',
  templateUrl: './headerbar.component.html',
  styleUrls: ['./headerbar.component.css']
})
export class HeaderBarComponent implements OnInit {
  username: string = "";

  constructor(private router: Router, private userService: UserService) { }

  ngOnInit() {
    this.username = this.userService.getUsername();
  }

  logout() {
    sessionStorage.clear();
    this.router.navigate(['/login']);
  }
}
