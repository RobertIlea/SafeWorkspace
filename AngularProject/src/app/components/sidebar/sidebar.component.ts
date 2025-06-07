import { Component, NgZoneOptions, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  standalone: false,
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent implements OnInit{
  userData: any = null;
  constructor(private router: Router){}
  ngOnInit(): void {
    const storedUser = sessionStorage.getItem('user');
    if(storedUser){
      const parsedUser = JSON.parse(storedUser);
      this.userData = parsedUser.user_data;
    }
  }

  logout() {
    localStorage.removeItem('jwtToken');
    sessionStorage.removeItem('user');
    this.router.navigate(['/login']);
  }
}
