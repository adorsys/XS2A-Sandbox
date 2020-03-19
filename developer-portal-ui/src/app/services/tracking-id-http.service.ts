import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {TrackingIdService} from "./tracking-id.service";
import {TrackingId} from "../models/tarckingId.model";

@Injectable({
  providedIn: 'root'
})
export class TrackingIdHttpService {

  constructor(private http: HttpClient, private trackingIdService: TrackingIdService) {
  }

  initializeApp(): Promise<any> {

    return new Promise(
      (resolve) => {
        this.http.get('assets/UI/trackingId.json')
          .toPromise()
          .then(response => {
              this.trackingIdService.trackingId = <TrackingId>response;
              resolve();
            }
          )
      }
    );
  }
}
