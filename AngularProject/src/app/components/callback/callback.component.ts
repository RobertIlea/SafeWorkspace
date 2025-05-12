import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { User } from '../../models/user-model';

@Component({
  selector: 'app-callback',
  standalone: false,
  
  templateUrl: './callback.component.html',
  styleUrl: './callback.component.css'
})
export class CallbackComponent {

  constructor (private routeSnapshot: ActivatedRoute,private router: Router){}

  ngOnInit(){
    const access_token = this.routeSnapshot.snapshot.queryParamMap.get('jwtToken');
    console.log("Acces Jwt token received");

    const user_data = this.routeSnapshot.snapshot.queryParamMap.get('user');
    console.log("User data: " + user_data);

    if(access_token && user_data){
      if(typeof sessionStorage !== 'undefined'){
        const sanitizedUserData = user_data.replace(/'/g, '"');
        const parsedUserData = JSON.parse(sanitizedUserData);

        const user = {
          user_data: parsedUserData as User,
        }

        console.log("User object:", user);
        sessionStorage.setItem('user',JSON.stringify(user));
        localStorage.setItem("jwtToken", access_token)
        this.router.navigate(['/home']);
      }else{
        console.log("Session storage is not supported");
        this.router.navigateByUrl('/login')
      }
    }else {
			console.log("Failed to retrieve access token and user data");
			this.router.navigateByUrl('/login');
		}    
  }
}
