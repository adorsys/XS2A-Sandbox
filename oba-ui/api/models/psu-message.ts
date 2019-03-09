/* tslint:disable */
export interface PsuMessage {
  category: 'ERROR' | 'WARNING' | 'INFO';
  code: string;
  path?: string;
  text?: string;
}
