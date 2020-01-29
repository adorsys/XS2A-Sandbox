/* tslint:disable */
export interface ChallengeDataTO {
  additionalInformation?: string;
  data?: Array<string>;
  image?: string;
  imageLink?: string;
  otpFormat?: 'characters' | 'integer';
  otpMaxLength?: number;
}
