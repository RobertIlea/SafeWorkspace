import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, RouterStateSnapshot } from '@angular/router';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';

interface JwtPayload {
  exp: number;
}

export const authGuard: CanActivateFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
  const router = inject(Router);
  const token = localStorage.getItem('jwtToken');

  if(!token){
    router.navigate(['/login']);
    return false;
  }

  try {
    // Decode the jt token
    const decoded = jwtDecode<JwtPayload>(token);
    const isExpired = decoded.exp <Date.now() / 1000;

    // If the token is expired, we remove it from localStorage
    if(isExpired){
      localStorage.removeItem('jwtToken');
      router.navigate(['/login']);
      return false;
    }
    return true;
  } catch (err){
    router.navigate(['/login']);
    return false;
  }
};
