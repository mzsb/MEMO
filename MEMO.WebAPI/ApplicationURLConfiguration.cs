using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.NetworkInformation;
using System.Threading.Tasks;

namespace MEMO.WebAPI
{
    public static class ApplicationURLConfiguration
    {
        public static string[] URLConfiguration(this string[] args) => GetIp(args);

        private static string[] GetIp(string[] args)
        {
            const string WIRELESS = "Wi-Fi";

            String ip = String.Empty;

            try
            {

                ip = NetworkInterface.GetAllNetworkInterfaces()
                                     .Where(ni => ni.NetworkInterfaceType
                                                    == NetworkInterfaceType.Wireless80211 && ni.Name == WIRELESS)
                                     .SingleOrDefault()
                                     .GetIPProperties()
                                     .UnicastAddresses.Where(ua => ua.Address.AddressFamily
                                                        == System.Net.Sockets.AddressFamily.InterNetwork)
                                                      .FirstOrDefault()
                                                      .Address.ToString();
            }
            catch (Exception e)
            {
                Console.WriteLine("Hiba történt az IPv4 cím lekérésekor.");
                Console.WriteLine(e);
            }

            if (ip == String.Empty)
            {
                ip = GetIpManually();
            }

            return SetUrl(args, ip);
        }

        private static string GetIpManually()
        {
            var ip = String.Empty;

            while (!ValidateIPv4(ip))
            {
                Console.Write("Physical device csatlakozás?(i/n)");
                var answer = Console.ReadLine();

                if (answer.ToLower() == "igen" || answer.ToLower() == "i") {
                    Console.WriteLine("Physical device csatlakoztatáshoz azonos Wi-Fi hálózatra kell csatlakoznia a mobileszköznek és a szervert futtató számítógépnek.");
                    Console.Write("A szerver gép hálózati IP címe (Windows: command prompt/ ipconfig/ Wireless LAN adapter Wi-Fi/ IPv4 Address): ");
                    ip = Console.ReadLine();
                }
                else {
                    ip = String.Empty;
                    break;
                }
            }

            return ip;
        }

        private static bool ValidateIPv4(string ip)
        {
            if (String.IsNullOrEmpty(ip))
            {
                return false;
            }

            var ipSegments = ip.Split('.');
            if (ipSegments.Length != 4)
            {
                Console.WriteLine("Helytelen ip cím formátum!");
                return false;
            }

            try
            {
                foreach (var ipSegment in ipSegments)
                {
                    var integerIpSegment = Int32.Parse(ipSegment);
                    if (integerIpSegment > 255 || integerIpSegment < 0)
                    {
                        Console.WriteLine("Helytelen ip cím formátum!");
                        return false;
                    }
                }
            }
            catch (FormatException e)
            {
                Console.WriteLine("Helytelen ip cím formátum!");
                return false;
            }

            return true;
        }

        private static string[] SetUrl(string[] args, string ip)
        {
            const string PORT = "5001";
            const string PROTOKOL = "https";
            string EMULATORURL = $"{PROTOKOL}://127.0.0.1:{PORT}";

            string[] newArgs = new string[args.Length + (args.Contains("--urls") ? 0 : 2)];

            if (args.Contains("--urls"))
            {
                for (int i = 0; i < args.Length; i++)
                {
                    if (args[i] == "--urls")
                    {
                        args[i + 1] = $"{args[i + 1]};{(ip != String.Empty ? $"{PROTOKOL}://{ip}:{PORT};" : "")}{EMULATORURL}";
                        break;
                    }
                }
            }
            else
            {
                for (int i = 0; i < args.Length; i++)
                {
                    newArgs[i] = args[i];
                }

                newArgs[newArgs.Length - 2] = "--urls";
                newArgs[newArgs.Length - 1] = $"{(ip != String.Empty ? $"{PROTOKOL}://{ip}:{PORT};" : "")}{EMULATORURL}";

                args = newArgs;
            }

            if (ip != String.Empty)
            {
                Console.WriteLine($"Physical device csatlakozás: {PROTOKOL}://{ip}:{PORT}");
            }
            else
            {
                Console.WriteLine("Physical device csatlakozás sikertelen");
            }

            Console.WriteLine($"Emulator csatlakozás: {EMULATORURL} \n");

            return args;
        }
    }
}
