using System;

namespace MEMO.DAL.Helpers
{
    public static class StringHelper
    {
        public static string CustomSubstring(this string str, int startIndex, int length, string[] removes = null)
        {
            str = str.Substring(startIndex, length);

            if (removes != null)
            {
                foreach (var s in removes)
                {
                    str = str.Replace(s, String.Empty);
                }
            }

            return str;
        }
    }
}
