﻿using System;

namespace Microsoft.Azure.Mobile.Crashes
{
    /// <summary>
    /// Exception thrown by <see cref="Crashes.GenerateTestCrash"/>.
    /// </summary>
    public class TestCrashException : Exception
    {
        private const string CrashMessage = "Test crash exception generated by SDK";

        /// <summary>
        /// Initializes a new instance of the TestCrashException class with a predefined error message.
        /// </summary>
        public TestCrashException(): base(CrashMessage)
        {
        }
    }
}
