import { EnvLink } from './envLinks.model';

export class Settings {
  constructor() {
    this.envLinks = [];
  }
  envLinks: EnvLink[];
}
