export function coerceBooleanProperty(value: any): boolean {
  return value != null && `${value}` !== 'false';
}
