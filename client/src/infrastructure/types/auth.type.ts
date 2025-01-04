export interface DecodedToken {
  id: string;
  userName: string;
  email: string;
  subscriptionType: string;
  profilePicture: string;
  roleCode: string;
  roleName: string;
  exp: number;
}

export interface UserInformation {
  id: string;
  userName: string;
  email: string;
  subscriptionType: string;
  profilePicture: string;
  roleCode: string;
  roleName: string;
}
