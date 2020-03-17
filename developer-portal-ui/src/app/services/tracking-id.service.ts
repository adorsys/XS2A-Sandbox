import {Injectable} from '@angular/core';
import {TrackingId} from "../models/tarckingId.model";

@Injectable({
  providedIn: 'root'
})
export class TrackingIdService {

  trackingId: TrackingId;

  constructor() {
    this.trackingId = new TrackingId();
  }
}
