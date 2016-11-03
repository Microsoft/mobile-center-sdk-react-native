﻿namespace Microsoft.Azure.Mobile
{
	using iOSMessageProvider = Microsoft.Azure.Mobile.iOS.Bindings.SNMLogMessageProvider;
	using iOSLogger = Microsoft.Azure.Mobile.iOS.Bindings.SNMWrapperLogger;

	public static partial class SonomaLog
	{
		public static void Verbose(string tag, string message)
		{
			iOSMessageProvider msg_provider = () => { return message; };
			iOSLogger.SNMWrapperLog(msg_provider, tag, Microsoft.Azure.Mobile.iOS.Bindings.SNMLogLevel.Verbose);
		}

		public static void Debug(string tag, string message)
		{
			iOSMessageProvider msg_provider = () => { return message; };
			iOSLogger.SNMWrapperLog(msg_provider, tag, Microsoft.Azure.Mobile.iOS.Bindings.SNMLogLevel.Debug);
		}

		public static void Info(string tag, string message)
		{
			iOSMessageProvider msg_provider = () => { return message; };
			iOSLogger.SNMWrapperLog(msg_provider, tag, Microsoft.Azure.Mobile.iOS.Bindings.SNMLogLevel.Info);
		}

		public static void Warn(string tag, string message)
		{
			iOSMessageProvider msg_provider = () => { return message; };
			iOSLogger.SNMWrapperLog(msg_provider, tag, Microsoft.Azure.Mobile.iOS.Bindings.SNMLogLevel.Warning);
		}

        public static void Error(string tag, string message)
        {
            iOSMessageProvider msg_provider = () => { return message; };
            iOSLogger.SNMWrapperLog(msg_provider, tag, Microsoft.Azure.Mobile.iOS.Bindings.SNMLogLevel.Error);
        }

        public static void Assert(string tag, string message)
        {
            iOSMessageProvider msg_provider = () => { return message; };
            iOSLogger.SNMWrapperLog(msg_provider, tag, Microsoft.Azure.Mobile.iOS.Bindings.SNMLogLevel.Assert);
        }
	}
}
