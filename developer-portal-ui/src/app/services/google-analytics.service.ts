import { Injectable } from '@angular/core';
import { EVENT_VALUE } from '../components/common/constant/constants';

declare let gtag;

@Injectable({
  providedIn: 'root',
})
export class GoogleAnalyticsService {
  enabled = false;

  constructor() {}

  sendSocialMediaClicks(social: any) {
    this.eventEmitter(social, 'redirect_to_social_media', 'click', social, EVENT_VALUE);
  }

  eventEmitter(eventName: string, eventCategory: string, eventAction: string, eventLabel: string = null, eventValue: number = null) {
    gtag('event', eventName, {
      eventCategory,
      eventLabel,
      eventAction,
      eventValue,
    });
  }
}
