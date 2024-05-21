export interface LoginRequest {
    username: string;
    password: string;
  }
  
  export interface SignupRequest {
    username: string;
    password: string;
    email: string;
  }
  
  export interface ForgetPasswordRequest {
    email: string;
  }
  
  export interface ChangePasswordRequest {
    oldPassword: string;
    newPassword: string;
  }
  