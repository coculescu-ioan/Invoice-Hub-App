import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent {
  isActive:boolean=false;
  userRole: string | null = '';

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.userRole = sessionStorage.getItem("user-role");
    console.log('User role:', this.userRole);
  }

  toggleSidebar() {
    this.isActive = !this.isActive;
  }
}
