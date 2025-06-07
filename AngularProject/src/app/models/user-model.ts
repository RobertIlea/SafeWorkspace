/**
 * User Model
 * This model represents a user in the application.
 */
export class User{
  /**
   * Unique identifier for the user
   */
  id: string | undefined;

  /**
   * Name of the user
   */
  name: string | undefined;

  /**
   * Email address of the user
   */
  email: string | undefined;

  /**
   * Phone number of the user
   */
  phone: string | undefined;

  /**
   * Constructor to initialize the User model
   * @param id - Unique identifier for the user
   * @param name - Name of the user
   * @param email - Email address of the user
   * @param phone - Phone number of the user
   */
  constructor(id: string, name: string, email: string, phone: string) {
      this.id = id;
      this.name = name;
      this.email = email;
      this.phone = phone;
  }
}
