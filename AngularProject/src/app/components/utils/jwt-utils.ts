export function decodeJwtToken(token:string){
    const payload = token.split('.')[1];
    const decodedPayload = atob(payload); 
    return JSON.parse(decodedPayload);
}