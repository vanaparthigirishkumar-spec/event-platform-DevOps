module.exports = {
  testEnvironment: 'jsdom',
  roots: ['<rootDir>/src'],
  testMatch: ['**/simple.test.ts'],
  moduleFileExtensions: ['ts', 'tsx', 'js', 'jsx', 'json'],
  testTimeout: 5000,
  forceExit: true,
  transform: {
    '^.+\\.tsx?$': 'ts-jest',
  },
};
