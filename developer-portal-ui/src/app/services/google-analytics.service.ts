import { Injectable } from '@angular/core';

declare let gtag;

@Injectable({
  providedIn: 'root',
})
export class GoogleAnalyticsService {
  enabled = false;

  constructor() {}

  public eventEmitter(eventName: string, eventCategory: string, eventAction: string, eventLabel: string = null, eventValue: number = null) {
    gtag('event', eventName, {
      eventCategory,
      eventLabel,
      eventAction,
      eventValue,
    });
  }
}
