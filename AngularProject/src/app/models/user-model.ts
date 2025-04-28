export class User{
    id: string | undefined;
    name: string | undefined;
    email: string | undefined;

    constructor(id: string, name: string, email: string) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}