/// <reference types="vitest/globals" />
/// <reference types="@testing-library/jest-dom" />

import { TestingLibraryMatchers } from '@testing-library/jest-dom/types/matchers';

declare module 'vitest' {
  interface Assertion<T = any> extends TestingLibraryMatchers<any, T> {}
  interface AsymmetricMatchersContaining extends TestingLibraryMatchers<any, any> {}
}