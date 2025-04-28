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
    const decoded = jwtDecode<JwtPayload>(token);
    const isExpired = decoded.exp <Date.now() / 1000;

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
