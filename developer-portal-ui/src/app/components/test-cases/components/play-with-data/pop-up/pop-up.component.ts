import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {NgbModal, NgbModalConfig} from '@ng-bootstrap/ng-bootstrap';
import {CertificateService} from '../../../../../services/certificate.service';
import {DataService} from '../../../../../services/data.service';

@Component({
  selector: 'app-pop-up',
  templateUrl: './pop-up.component.html',
  styleUrls: ['./pop-up.component.scss'],
  providers: [NgbModalConfig, NgbModal],
})
export class PopUpComponent implements OnInit {
  @Input() certificate;
  @Input() default;
  @Output() onEditCertificate = new EventEmitter();

  constructor(
    config: NgbModalConfig,
    private modalService: NgbModal,
    private dataService: DataService,
    private certificateService: CertificateService
  ) {
    config.backdrop = 'static';
    config.keyboard = false;
  }

  open(content) {
    this.modalService.open(content);
  }

  restoreCertificate() {
    this.certificateService.getQwacCertificate().toPromise()
      .then(data => {
        this.certificateService.storeCertificate(data);
        this.certificateService.setDefault(false);
        this.certificate = data;
        this.dataService.showToast('Certificate restored to default', 'Certificate restored!', 'success');
      });
  }

  saveCertificate() {
    this.onEditCertificate.emit(this.certificate);
    this.certificateService.storeCertificate(this.certificate);
    this.certificateService.setDefault(false);
    this.dataService.showToast(
      'Certificate edited and saved',
      'Success!',
      'success'
    );
  }

  clearCertificate() {
    this.certificateService.removeCertificate();
    this.certificate = '';
    this.dataService.showToast('Certificate deleted', 'Success!', 'success');
  }

  ngOnInit() {
  }
}
