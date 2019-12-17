import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-get-started',
  templateUrl: './embedded.component.html',
  styleUrls: ['./embedded.component.scss'],
})
export class EmbeddedComponent implements OnInit {
  thumbImage = '../../../../assets/images/embedded_pis_initiation_thumb.svg';
  fullImage = '../../../../assets/images/embedded_pis_initiation.svg';
  mode = 'hover-freeze';

  constructor() {}
  ngOnInit() {}
}
