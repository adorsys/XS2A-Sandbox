import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Language } from '../../../models/language';

@Injectable({
  providedIn: 'root',
})
export class LanguageService {
  private language = Language.en;

  private subject = new BehaviorSubject(Language.en);

  constructor() {}

  setLanguage(newLanguage: Language) {
    this.language = newLanguage;
    this.subject.next(newLanguage);
  }

  getLanguage$(): Observable<Language> {
    return this.subject.asObservable();
  }
}
