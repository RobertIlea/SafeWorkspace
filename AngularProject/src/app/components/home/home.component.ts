import { Component, HostListener } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { trigger, style, animate, transition } from '@angular/animations';

@Component({
  selector: 'app-home',
  standalone: false,
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
  animations: [
    trigger('cardAnimation', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate('400ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ])
  ]
})
export class HomeComponent {
  isDarkMode = false;
  isSidebarVisible = true;
  isMobileView = false;
  userData: any = null;
  constructor(private route: ActivatedRoute, private router: Router) {}
  ngOnInit(): void {
    this.checkWindowSize();
  }

  @HostListener('window:resize')
  onResize(){
    this.checkWindowSize();
  }

  checkWindowSize(){
    this.isMobileView = window.innerWidth <= 768;
    
    if(this.isMobileView){
      this.isSidebarVisible = false;
    }else{
      this.isSidebarVisible = true;
    }
  }

  toggleSidebar(){
    this.isSidebarVisible = !this.isSidebarVisible;
  }

  toggleDarkMode(){
    this.isDarkMode = !this.isDarkMode;
  }
}
