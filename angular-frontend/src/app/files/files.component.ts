import {Component, OnInit} from '@angular/core';
import { Router } from '@angular/router';
import {UserService} from "../services/user.service";

@Component({
  selector: 'files',
  templateUrl: './files.component.html',
  styleUrl: './files.component.css'
})
export class FilesComponent implements OnInit {

  files: any[] = [];
  constructor(
    private router: Router,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.userService.getFiles().subscribe(response => {
      this.files = response;
      console.log(this.files);
    });
  }
}
