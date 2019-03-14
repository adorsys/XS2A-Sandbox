import { CanActivate } from '@angular/router';
import { ConfigService } from './services/config.service';
import { Config } from '../../models/config';
import { Injectable } from '@angular/core';

@Injectable()
export class FeatureEnabledGuard implements CanActivate {
  public config: Config;

  constructor(private configService: ConfigService) {
    this.config = configService.getConfig();
  }

  canActivate() {
    return this.config.certPageEnabled;
  }
}
