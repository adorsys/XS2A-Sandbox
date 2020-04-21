import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class LocalStorageService {
  constructor() {}

  static clear() {
    return localStorage.clear();
  }

  static get(key: string): any {
    if (key !== undefined) {
      return localStorage.getItem(key);
    }
  }

  static remove(key: string) {
    return localStorage.removeItem(key);
  }

  static set(key: string, value: any) {
    return localStorage.setItem(key, value);
  }
}
