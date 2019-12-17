import { Component } from '@angular/core';

@Component({
  selector: 'app-welcome',
  templateUrl: './redirect.component.html',
  styleUrls: ['./redirect.component.scss'],
})
export class RedirectComponent {
  thumbImage = '../../../../assets/images/redirect_pis_initiation_thumb.svg';
  fullImage = '../../../../assets/images/redirect_pis_initiation.svg';
  mode = 'hover-freeze';
}
