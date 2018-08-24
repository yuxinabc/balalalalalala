package com.my51c.see51.data;

import com.my51c.see51.protocal.Utils;

import java.nio.ByteBuffer;

public class Device3GInfo implements Cloneable {

		private int nCmd;	
		private byte szPacketFlag[]; 
		private byte szDeviceName[]; 		
		private byte szDeviceType[]; 	
		private int nMaxChannel; 	 	
		private byte szDeviceIP[]; 
		private byte szDeviceMasK[]; 
		private byte szDeviceGateWay[]; 	
		private byte szMultiAddr[]; 
//		private byte szMacAddr[]; 
		private byte szMacAddr_LAN[];
		private byte szMacAddr_WIFI[]; 
		private int nEnableDeviceDHCP;
		private byte szRevsered0[]; 
		private byte szDNS0[]; 
		private byte szDNS1[]; 		
		private int nMultiPort; 
		private int nDataPort; 
		private int nWebServerPort; 

		private byte szUserName[]; 	
		private byte szPwd[]; 
		private byte szCameraVer[]; 
		
		
		private byte szWanServerIP[]; 
		private byte szServerPort[]; 	
		private byte szCamSerial[]; 
		private byte szTutkID[];	//TutkID
		private int nSdTotal;
		private int nSdFree;
		private byte nBattery;
		private byte szRevsered1[];
		
//		private int nEnableWiFiDHCP;
//		private int nEnableWiFi; 
//		private int nWiFiEncryMode; 
//		private byte szWiFiIP[]; 
//		private byte szWiFiSSID[]; 
//		private byte szWiFiPwd[]; 
//
//		private byte szWiFiMasK[];	
		private byte szWiFiGateWay[]; 	
		private byte szWiFiDNS0[]; 
//		private byte szWiFiDNS1[]; //
		private byte sz3GUser[];
		private byte sz3GPWD[];
		private byte sz3GAPN[];
		private byte szDialNum[];
		private byte szAlarmNum0[];
		private byte szAlarmNum1[];
		private byte szAlarmNum2[];
		private byte szAlarmNum3[];
		
		/*
		 * //char* pVideoSize[] = { bit 0 1 2 3 4 5 "H264:720P", "H264:D1",
		 * "H264:VGA", "H264:DCIF", "H264:CIF", "H264:QVGA", bit 6 7 8
		 * "H264:720P,H264:VGA", "H264:720P,H264:QVGA(320*240)",
		 * "H264:720P,H264:CIF(352*288)", bit 9 10 11 12
		 * "H264:D1(704*576),H264:DCIF", "H264:D1,H264:CIF", "H264:VGA,H264:QVGA",
		 * "H264:CIF,H264:QVGA" };
		 */
		private int uOfferSize; 		
		private int uImageSize; 		
		private int uMirror;
		private int uFlip; 
		private int uRequestStream;//
		private int uBitrate1; 	
		private short uFramerate1Val;	
		private short uFramerate1; 
			
		private int uBitrate2; 	
		private short uFramerate2Val; 
		private short uFramerate2; 

		private int uImagesource; 
		private int uChangePWD; // 1: need to change 0: not to change
		private byte szNewPwd[]; // the new password
		private int nDeviceNICType; // 0 wired NIC;1 wifi NIC
		private int uEnableAudio; 
		//private byte szRevsered1[];
		private byte bgioinenable;	 ///< GIO input enable
		private byte bgiointype;	 ///< GIO input type
		private byte bgiooutenable;	 ///< GIO output enable
		private byte bgioouttype;	 ///< GIO output type
		private byte bAlarmEnable;	 ///alarm enable or disable
		private byte cRs485baudrate; ///0-9600 1-4800 2-2400  3-1200, 4-disable-gpio 5-disable-rs232
		private short uSnapInterval;	 ///from 1minute to 24hour=24*60, 0--disable, 2014-01-26
		private byte nAudioEncoderType; ///0--G711 1--aac 2--adpcm 3--pcm
		private byte uCloudSaveType; ///0--none,  1--sd, 2--cloud, server 3- all
		private byte uCloudSaveStream; ///0--big 1--small
		private short uCloudSaveTime; ///0--5, n>1--10second*n
		///////rate control, if you don't know how to use it, please don't touch these paras...
		private byte nRateControl1;//0--auto 1--vbr 2--cbr
		private byte nRateControl2;//0--auto 1--vbr 2--cbr
		private byte nQPMax1;
		private byte nQPMin1;
		private byte nQPMax2;
		private byte nQPMin2;
		private byte nIPRatio1;//
		private byte nIPRatio2;
		///////rate control end
		//////OSD, 20140513
		private byte bOSDTimeStampEnable1;
		private byte bOSDDateStampEnable1;
		private byte bOSDTimeStampEnable2;
		private byte bOSDDateStampEnable2;
		//////RTSP on, 20140702
		private byte bRTST_On;
		//////bright contrast  and saturation
		private byte uBrightness;
		private byte uContrast;
		private byte uSaturation;
		////ir light
		private byte nIRLightControlMethod;//0-auto, 1-on, 2-off
		private byte bFormatSD;//1-format sd
		//////
		private byte szRevsered2[];
		private byte nAlarmAudioPlay; // /< alarm audio play enable/disable
		private byte nAlarmDuration; // /< alarm duration 0~5{10, 30, 60, 300, 600,
										// NON_STOP_TIME}
		private byte bAlarmUploadFTP; //
		private byte bAlarmSaveToSD; // 
		private byte bSetFTPSMTP; // 
		private byte servier_ip[]; // 
		private byte username[]; // /< FTP or SMTP login username
	    private byte password[]; // /< FTP or SMTP login password  
		private int uPort; // /< FTP or SMTP

		// GVAP/
		private byte szBindAccont[];
		private byte szDevSAddr[]; 		
		private int uDevSPort;

		private byte szSMTPReceiver[]; 
		private byte motionenable; // /< motion detection enable
		private byte motioncenable; // /< customized sensitivity enable
		private byte motionlevel; // /< predefined sensitivity level
		private byte motioncvalue; // /< customized sensitivity value
		private byte motionblock[]; // /< motion detection block data
		private byte bDeviceRest; //		
		private byte bEnableEmailRcv; // 
		private byte bAttachmentType; 
		//
		private byte ntp_timezone; 									
		private int nYear; // 	
		private byte nMon; 
							
		private byte nDay; // /< Second from 1 to 31.
		private byte nHour; // /< Hour from 0 to 23.
		private byte nMin; // /< Minute from 0 to 59.
		private byte nSec; // /< Second from 0 to 59.

		private byte nSdinsert; 
		private byte bSchedulesUploadFTP; 
		private byte bSchedulesSaveToSD;
		Schedule aSchedules[]; // /< schedule data

		// private byte szRevsered2[];

		public Device3GInfo()
		{
			szPacketFlag = new byte[24];
			szDeviceName = new byte[20]; 			
			szDeviceType = new byte[24]; 			
			szDeviceIP = new byte[16]; 
			szDeviceMasK = new byte[16]; 			
			szDeviceGateWay = new byte[16]; 		
			szMultiAddr = new byte[16]; 
			szMacAddr_LAN = new byte[8]; 
			szMacAddr_WIFI = new byte[8]; 
//			nEnableDeviceDHCP = Utils.ntohi(byteBuf.getInt()); 
			szRevsered0 =  new byte[12]; // 
			szDNS0 = new byte[16]; 
			szDNS1 = new byte[16]; 			
			szUserName = new byte[16]; 		
			szPwd = new byte[16]; 
			szCameraVer = new byte[8]; 			
			szWanServerIP = new byte[24]; 
			szServerPort = new byte[8]; 		
			szCamSerial = new byte[24];  
			szTutkID = new byte[24];
			szRevsered1 = new byte[16];
			
//			szWiFiIP = new byte[20]; 
//			szWiFiSSID = new byte[128]; 
//			szWiFiPwd = new byte[68]; 
//			szWiFiMasK = new byte[16]; 
//			szWiFiGateWay = new byte[16]; 
//			szWiFiDNS0 = new byte[16]; 
//			szWiFiDNS1 = new byte[16]; //
			
			
			sz3GUser = new byte[40];
			sz3GPWD = new byte[40];
			sz3GAPN = new byte[108];
			szDialNum = new byte[24];
			szAlarmNum0 = new byte[20];
			szAlarmNum1 = new byte[20];
			szAlarmNum2 = new byte[20];
			szAlarmNum3 = new byte[20];
			
			szNewPwd = new byte[16]; // the new password
			szRevsered2 = new byte[16];

			setServier_ip(new byte[37]);
			setUsername(new byte[16]);
			setPassword(new byte[16]);
			szBindAccont = new byte[48]; 
			szDevSAddr = new byte[48];
			setSzSMTPReceiver(new byte[64]);
			motionblock = new byte[4];
			aSchedules = new Schedule[8];
			for (int i = 0; i < 8; i++)
			{
				aSchedules[i] = new Schedule();
			}
			// szRevsered2 = new byte[204];
		}
		
		public Device3GInfo(ByteBuffer byteBuf)
		{
			szPacketFlag = new byte[24]; 
			szDeviceName = new byte[20]; 		
			szDeviceType = new byte[24]; 			
			szDeviceIP = new byte[16]; 
			szDeviceMasK = new byte[16]; 		
			szDeviceGateWay = new byte[16]; 			
			szMultiAddr = new byte[16]; 
	//by marshal
//			szMacAddr = new byte[32]; 
			szMacAddr_LAN = new byte[8]; 	
			szMacAddr_WIFI = new byte[8]; 	
//			nEnableDeviceDHCP = Utils.ntohi(byteBuf.getInt()); 
			szRevsered0 =  new byte[12]; // 
	///end		
			szDNS0 = new byte[16]; 
			szDNS1 = new byte[16]; 			
			szUserName = new byte[16]; 		
			szPwd = new byte[16]; 
			szCameraVer = new byte[8]; 			
			szWanServerIP = new byte[24]; 
			szServerPort = new byte[8]; 			
			szCamSerial = new byte[24]; 
			szTutkID = new byte[24];
			szRevsered1 = new byte[16];
			
//			szWiFiIP = new byte[20]; 
//			szWiFiSSID = new byte[128]; 
//			szWiFiPwd = new byte[68]; 
//			szWiFiMasK = new byte[16]; 
//			szWiFiGateWay = new byte[16]; 
//			szWiFiDNS0 = new byte[16]; 
//			szWiFiDNS1 = new byte[16]; //
			sz3GUser = new byte[40];
			sz3GPWD = new byte[40];
			sz3GAPN = new byte[108];
			szDialNum = new byte[24];
			szAlarmNum0 = new byte[20];
			szAlarmNum1 = new byte[20];
			szAlarmNum2 = new byte[20];
			szAlarmNum3 = new byte[20];
			
			szNewPwd = new byte[16]; // the new password
			szRevsered2 = new byte[16];

			setServier_ip(new byte[37]);
			setUsername(new byte[16]);
			setPassword(new byte[16]);
			szBindAccont = new byte[48]; 
			szDevSAddr = new byte[48];
			setSzSMTPReceiver(new byte[64]);
			motionblock = new byte[4];

			// aSchedules = new Schedule[8];
			// szRevsered2 = new byte[204];

			nCmd = Utils.ntohi(byteBuf.getInt()); 			
			byteBuf.get(szPacketFlag, 0, 24); 
			byteBuf.get(szDeviceName, 0, 20);
			byteBuf.get(szDeviceType, 0, 24); 
			nMaxChannel = Utils.ntohi(byteBuf.getInt()); 		
			byteBuf.get(szDeviceIP, 0, 16); 
			byteBuf.get(szDeviceMasK, 0, 16);		
			byteBuf.get(szDeviceGateWay, 0, 16); 			
			byteBuf.get(szMultiAddr, 0, 16); 
	//by marshal	
//			byteBuf.get(szMacAddr, 0, 32); 
			byteBuf.get(szMacAddr_LAN, 0, 8);
			byteBuf.get(szMacAddr_WIFI, 0, 8);
			nEnableDeviceDHCP = Utils.ntohi(byteBuf.getInt()); 
			byteBuf.get(szRevsered0, 0, 12); 
	///end		

			byteBuf.get(szDNS0, 0, 16); 
			byteBuf.get(szDNS1, 0, 16); 		
			nMultiPort = Utils.ntohi(byteBuf.getInt()); 			
			nDataPort = Utils.ntohi(byteBuf.getInt()); 
			nWebServerPort = Utils.ntohi(byteBuf.getInt()); 

			byteBuf.get(szUserName, 0, 16); 
			byteBuf.get(szPwd, 0, 16); 
			byteBuf.get(szCameraVer, 0, 8); 
			byteBuf.get(szWanServerIP, 0, 24); 
			byteBuf.get(szServerPort, 0, 8); 		
			byteBuf.get(szCamSerial, 0, 24); 
			byteBuf.get(szTutkID, 0, 24);
			nSdTotal = Utils.ntohi(byteBuf.getInt());
			nSdFree = Utils.ntohi(byteBuf.getInt());
			nBattery = byteBuf.get();
			byteBuf.get(szRevsered1, 0, 7);
//			nEnableWiFiDHCP = Utils.ntohi(byteBuf.getInt()); 
//			nEnableWiFi = Utils.ntohi(byteBuf.getInt());
//			nWiFiEncryMode = Utils.ntohi(byteBuf.getInt()); 
//			byteBuf.get(szWiFiIP, 0, 20); 
//			byteBuf.get(szWiFiSSID, 0, 128); 
//			byteBuf.get(szWiFiPwd, 0, 68); 
////			nEnableDeviceDHCP = Utils.ntohi(byteBuf.getInt());
//			byteBuf.get(szWiFiMasK, 0, 16); 
//			byteBuf.get(szWiFiGateWay, 0, 16); 
//			byteBuf.get(szWiFiDNS0, 0, 16); 
//			byteBuf.get(szWiFiDNS1, 0, 16); //
			byteBuf.get(sz3GUser, 0, 40);
			byteBuf.get(sz3GPWD, 0, 40);
			byteBuf.get(sz3GAPN, 0, 108);
			byteBuf.get(szDialNum, 0, 24);
			byteBuf.get(szAlarmNum0, 0, 20);
			byteBuf.get(szAlarmNum1, 0, 20);
			byteBuf.get(szAlarmNum2, 0, 20);
			byteBuf.get(szAlarmNum3, 0, 20);
			
			uOfferSize = Utils.ntohi(byteBuf.getInt()); 			
			uImageSize = Utils.ntohi(byteBuf.getInt()); 			
			uMirror = Utils.ntohi(byteBuf.getInt()); 
			uFlip = Utils.ntohi(byteBuf.getInt()); 
			uRequestStream = Utils.ntohi(byteBuf.getInt());//
			uBitrate1 = Utils.ntohi(byteBuf.getInt()); //
			uFramerate1Val = Utils.ntohs(byteBuf.getShort()); 		
			uFramerate1 = Utils.ntohs(byteBuf.getShort()); 
			uBitrate2 = Utils.ntohi(byteBuf.getInt()); 		
			uFramerate2Val = Utils.ntohs(byteBuf.getShort()); 		
			uFramerate2 = Utils.ntohs(byteBuf.getShort());
			uImagesource = Utils.ntohi(byteBuf.getInt()); 
			uChangePWD = Utils.ntohi(byteBuf.getInt()); // 1: need to change 0: not
														// to change
			byteBuf.get(szNewPwd, 0, 16); // the new password
			nDeviceNICType = Utils.ntohi(byteBuf.getInt()); // 0 wired NIC =
															// Utils.ntohi(byteBuf.getInt());1
															// wifi NIC
			uEnableAudio = Utils.ntohi(byteBuf.getInt()); 			
			bgioinenable = byteBuf.get();	 ///< GIO input enable
			bgiointype = byteBuf.get();	 	 ///< GIO input type
			bgiooutenable = byteBuf.get();	 ///< GIO output enable
			bgioouttype = byteBuf.get();	 ///< GIO output type
			bAlarmEnable = byteBuf.get();	 ///alarm enable or disable
			cRs485baudrate = byteBuf.get(); ///0-9600 1-4800 2-2400  3-1200, 4-disable-gpio 5-disable-rs232
			uSnapInterval = Utils.ntohs(byteBuf.getShort());					///from 1minute to 24hour=24*60, 0--disable, 2014-01-26
			nAudioEncoderType = byteBuf.get();					///0--G711 1--aac 2--adpcm 3--pcm
			uCloudSaveType = byteBuf.get();					///0--none,  1--sd, 2--cloud, server 3- all
			uCloudSaveStream = byteBuf.get();					///0--big 1--small
			uCloudSaveTime = Utils.ntohs(byteBuf.getShort());				///0--5, n>1--10second*n
			///////rate control, if you don't know how to use it, please don't touch these paras...
			nRateControl1 = byteBuf.get();//0--auto 1--vbr 2--cbr
			nRateControl2 = byteBuf.get();//0--auto 1--vbr 2--cbr
			nQPMax1 = byteBuf.get();
			nQPMin1 = byteBuf.get();
			nQPMax2 = byteBuf.get();
			nQPMin2 = byteBuf.get();
			nIPRatio1 = byteBuf.get();
			nIPRatio2 = byteBuf.get();
			///////rate control end
			//////OSD, 20140513
			bOSDTimeStampEnable1 = byteBuf.get();
			bOSDDateStampEnable1 = byteBuf.get();
			bOSDTimeStampEnable2 = byteBuf.get();
			bOSDDateStampEnable2 = byteBuf.get();
			//////RTSP on, 20140702
			bRTST_On = byteBuf.get();
			//////bright contrast  and saturation
			uBrightness = byteBuf.get();
			uContrast = byteBuf.get();
			uSaturation = byteBuf.get();
			////ir light
			nIRLightControlMethod = byteBuf.get();//0-auto, 1-on, 2-off
			bFormatSD = byteBuf.get();///1-format sd
			//////
			byteBuf.get(szRevsered2, 0, 15);

			setnAlarmAudioPlay(byteBuf.get());
			nAlarmDuration = byteBuf.get();
			setbAlarmUploadFTP(byteBuf.get());
			setbAlarmSaveToSD(byteBuf.get());
			setbSetFTPSMTP(byteBuf.get());
			byteBuf.get(getServier_ip(), 0, 37);
			byteBuf.get(getUsername(), 0, 16);
			byteBuf.get(getPassword(), 0, 16);
			setuPort(Utils.ntohi(byteBuf.getInt()));

			byteBuf.get(szBindAccont, 0, 48);
			byteBuf.get(szDevSAddr, 0, 48);
			uDevSPort = Utils.ntohi(byteBuf.getInt());
			byteBuf.get(getSzSMTPReceiver(), 0, 64);
			setMotionenable(byteBuf.get());
			motioncenable = byteBuf.get();
			setMotionlevel(byteBuf.get());
			motioncvalue = byteBuf.get();
			byteBuf.get(motionblock, 0, 4);
			setbDeviceRest(byteBuf.get());
			setbEnableEmailRcv(byteBuf.get());
			setbAttachmentType(byteBuf.get());
			setNtp_timezone(byteBuf.get());
			setnYear(Utils.ntohi(byteBuf.getInt()));
			setnMon(byteBuf.get());
			setnDay(byteBuf.get());
			setnHour(byteBuf.get());
			setnMin(byteBuf.get());
			setnSec(byteBuf.get());
			setnSdinsert(byteBuf.get());
			setbSchedulesUploadFTP(byteBuf.get());
			setbSchedulesSaveToSD(byteBuf.get());
				
			aSchedules = new Schedule[8];
			for (int i = 0; i < 8; i++)
			{
				byte buf[] = new byte[8];
				byteBuf.get(buf, 0, 8);
				aSchedules[i] = new Schedule(buf);
			}
			// byteBuf.get(szRevsered2, 0, 204);
			
			//printflog();
		}

		public ByteBuffer toByteBuffer()
		{
			ByteBuffer retBuf = ByteBuffer.allocate(1088);
			retBuf.putInt(Utils.htoni(nCmd)); 
			retBuf.put(szPacketFlag, 0, 24); 
			retBuf.put(szDeviceName, 0, 20); 		
			retBuf.put(szDeviceType, 0, 24); 			
			retBuf.putInt(Utils.htoni(nMaxChannel)); 			
			retBuf.put(szDeviceIP, 0, 16); 
			retBuf.put(szDeviceMasK, 0, 16); 			
			retBuf.put(szDeviceGateWay, 0, 16); 			
			retBuf.put(szMultiAddr, 0, 16); 
	//by marshal	
//			retBuf.put(szMacAddr, 0, 32);
			retBuf.put(szMacAddr_LAN, 0, 8); 
			retBuf.put(szMacAddr_WIFI, 0, 8); 
			retBuf.putInt(Utils.htoni(nEnableDeviceDHCP)); 
			retBuf.put(szRevsered0, 0, 12); // reserved
	///end		
			retBuf.put(szDNS0, 0, 16); 
			retBuf.put(szDNS1, 0, 16); 			
			retBuf.putInt(Utils.htoni(nMultiPort)); 			
			retBuf.putInt(Utils.htoni(nDataPort)); 
			retBuf.putInt(Utils.htoni(nWebServerPort)); 

			retBuf.put(szUserName, 0, 16); 
			retBuf.put(szPwd, 0, 16); 
			retBuf.put(szCameraVer, 0, 8); 
			retBuf.put(szWanServerIP, 0, 24); 
			retBuf.put(szServerPort, 0, 8); 			
			retBuf.put(szCamSerial, 0, 24); 
			retBuf.put(szTutkID, 0, 24);
			retBuf.putInt(Utils.htoni(nSdTotal));
			retBuf.putInt(Utils.htoni(nSdFree));
			retBuf.put(nBattery);
			retBuf.put(szRevsered1, 0, 7);
			
//			retBuf.putInt(Utils.htoni(nEnableWiFiDHCP)); 
//			retBuf.putInt(Utils.htoni(nEnableWiFi)); 
//			retBuf.putInt(Utils.htoni(nWiFiEncryMode)); 
//			retBuf.put(szWiFiIP, 0, 20); 
//			retBuf.put(szWiFiSSID, 0, 128); 
//			retBuf.put(szWiFiPwd, 0, 68); 
////			retBuf.putInt(Utils.htoni(nEnableDeviceDHCP));
//			retBuf.put(szWiFiMasK, 0, 16); 
//			retBuf.put(szWiFiGateWay, 0, 16); 
//			retBuf.put(szWiFiDNS0, 0, 16); 
//			retBuf.put(szWiFiDNS1, 0, 16); //
			
			
		
			retBuf.put(sz3GUser, 0, 40);
			retBuf.put(sz3GPWD, 0, 40);
			retBuf.put(sz3GAPN, 0, 108);
			retBuf.put(szDialNum, 0, 24);
			retBuf.put(szAlarmNum0, 0, 20);
			retBuf.put(szAlarmNum1, 0, 20);
			retBuf.put(szAlarmNum2, 0, 20);
			retBuf.put(szAlarmNum3, 0, 20);
			

			retBuf.putInt(Utils.htoni(uOfferSize)); 
		  retBuf.putInt(Utils.htoni(uImageSize)); 			
		  retBuf.putInt(Utils.htoni(uMirror)); 
			retBuf.putInt(Utils.htoni(uFlip)); 
			retBuf.putInt(Utils.htoni(uRequestStream));//
			retBuf.putInt(Utils.htoni(uBitrate1)); 	
			retBuf.putShort(Utils.htons(uFramerate1Val)); 		
			retBuf.putShort(Utils.htons(uFramerate1));
			retBuf.putInt(Utils.htoni(uBitrate2)); 	
			retBuf.putShort(Utils.htons(uFramerate2Val)); 		
			retBuf.putShort(Utils.htons(uFramerate2)); 

			retBuf.putInt(Utils.htoni(uImagesource)); 
			retBuf.putInt(Utils.htoni(uChangePWD)); // 1: need to change 0: not to
													// change
			retBuf.put(szNewPwd, 0, 16); // the new password
			retBuf.putInt(Utils.htoni(nDeviceNICType)); // 0 wired NIC);1 wifi NIC
			retBuf.putInt(Utils.htoni(uEnableAudio)); 
			retBuf.put(bgioinenable);	 ///< GIO input enable
			retBuf.put(bgiointype);	 ///< GIO input type
			retBuf.put(bgiooutenable);	 ///< GIO output enable
			retBuf.put(bgioouttype);	 ///< GIO output type
			retBuf.put(bAlarmEnable);	 ///alarm enable or disable
			retBuf.put(cRs485baudrate); ///0-9600 1-4800 2-2400  3-1200, 4-disable-gpio 5-disable-rs232
			retBuf.putShort(Utils.htons(uSnapInterval));					///from 1minute to 24hour=24*60, 0--disable, 2014-01-26
			retBuf.put(nAudioEncoderType);					///0--G711 1--aac 2--adpcm 3--pcm
			retBuf.put(uCloudSaveType);					///0--none,  1--sd, 2--cloud, server 3- all
			retBuf.put(uCloudSaveStream);					///0--big 1--small
			retBuf.putShort(Utils.htons(uCloudSaveTime));					///0--5, n>1--10second*n
			///////rate control, if you don't know how to use it, please don't touch these paras...
			retBuf.put(nRateControl1);//0--auto 1--vbr 2--cbr
			retBuf.put(nRateControl2);//0--auto 1--vbr 2--cbr
			retBuf.put(nQPMax1);
			retBuf.put(nQPMin1);
			retBuf.put(nQPMax2);
			retBuf.put(nQPMin2);
			retBuf.put(nIPRatio1);//
			retBuf.put(nIPRatio2);
			///////rate control end
			//////OSD, 20140513
			retBuf.put(bOSDTimeStampEnable1);
			retBuf.put(bOSDDateStampEnable1);
			retBuf.put(bOSDTimeStampEnable2);
			retBuf.put(bOSDDateStampEnable2);
			//////RTSP on, 20140702
			retBuf.put(bRTST_On);
			//////bright contrast  and saturation
			retBuf.put(uBrightness);
			retBuf.put(uContrast);
			retBuf.put(uSaturation);
			////ir light
			retBuf.put(nIRLightControlMethod);//0-auto, 1-on, 2-off
			retBuf.put(bFormatSD);//1-format sd
			retBuf.put(szRevsered2, 0, 15);
			
			retBuf.put(getnAlarmAudioPlay());
			retBuf.put(nAlarmDuration);
			retBuf.put(getbAlarmUploadFTP());
			retBuf.put(getbAlarmSaveToSD());
			retBuf.put(getbSetFTPSMTP());
			retBuf.put(getServier_ip(), 0, 37);
			retBuf.put(getUsername(), 0, 16);
			retBuf.put(getPassword(), 0, 16);
			retBuf.putInt(Utils.htoni(getuPort()));

			retBuf.put(szBindAccont, 0, 48); 
			// retBuf.put(szRevsered2, 0, 204);
			retBuf.put(szDevSAddr, 0, 48);

			retBuf.putInt(Utils.htoni(uDevSPort));
			retBuf.put(getSzSMTPReceiver(), 0, 64);

			retBuf.put(getMotionenable());
			retBuf.put(motioncenable);
			retBuf.put(getMotionlevel());
			retBuf.put(motioncvalue);
			retBuf.put(motionblock, 0, 4);
			retBuf.put(getbDeviceRest());
			retBuf.put(getbEnableEmailRcv());
			retBuf.put(getbAttachmentType());
			retBuf.put(getNtp_timezone());

			retBuf.putInt(Utils.htoni(getnYear()));
			retBuf.put(getnMon());
			retBuf.put(getnDay());
			retBuf.put(getnHour());
			retBuf.put(getnMin());
			retBuf.put(getnSec());
			retBuf.put(getnSdinsert());
			retBuf.put(getbSchedulesUploadFTP());
			retBuf.put(getbSchedulesSaveToSD());

			for (int i = 0; i < 8; i++)
			{
				retBuf.put(aSchedules[i].getScheduleBuffer(), 0, 8);
			}

			retBuf.flip();
			return retBuf;
		}

		protected String byteToString(byte[] src)
		{
			int len = 0;
			for (; len < src.length; len++)
			{
				if (src[len] == 0)
				{
					break;
				}
			}
			return new String(src, 0, len);
		}

		protected byte[] StringToByte(String str, int length)
		{
			byte retByte[] = new byte[length];
			byte strByte[] = str.getBytes();
			for (int i = 0; i < strByte.length && i < length; i++)
			{
				retByte[i] = strByte[i];
			}
			return retByte;
		}
		
		public void printflog()
		{	
			System.out.println("**********************device info********************");
			System.out.println("nCmd:"+getCmd());
			System.out.println("szPacketFlag:"+ getPacketFlag());
			System.out.println("szDeviceName:"+ getDeviceName());
			System.out.println("szDeviceType:"+ getDeviceType());
			System.out.println("nMaxChannel:"+ getMaxChannel());
			System.out.println("szDeviceIP:"+getDeviceIP());
			System.out.println("szDeviceMasK:"+getDeviceMasK());
			System.out.println("szDeviceGateWay:"+getDeviceGateWay());
			System.out.println("szMultiAddr:"+getMultiAddr());
			System.out.println("szMacAddr_LAN:"+getMacAddr_LAN());
			System.out.println("szMacAddr_WIFI:"+getMacAddr_WIFI());
			System.out.println("nEnableDeviceDHCP:"+getEnableDeviceDHCP());
			System.out.println("szDNS0:"+getDNS0());
			System.out.println("szDNS1:"+getDNS1());
			System.out.println("nMultiPort:"+getMultiPort());
			System.out.println("nDataPort:"+getDataPort());
			System.out.println("nWebServerPort:"+getWebServerPort());
			System.out.println("szUserName:"+getszUserName());
			System.out.println("szPwd:"+getPwd());
			System.out.println("szCameraVer:"+getCameraVer());
			System.out.println("szWanServerIP:"+getWanServerIP());
			System.out.println("szServerPort:"+getServerPort());
			System.out.println("szCamSerial:"+getCamSerial());
			System.out.println("szTutkID:"+getTutkID());
			System.out.println("nSdTotal:"+getSdTotal());
			System.out.println("nSDFree:"+getSdFree());
			System.out.println("nBattery:"+getBattery());
			System.out.println("sz3GUser:"+getsz3GUser());
			System.out.println("sz3GPWD:"+getsz3GPWD());
			System.out.println("sz3GAPN:"+getsz3GAPN());
			System.out.println("szDialNum:"+getszDialNum());
			System.out.println("szAlarmNum0:"+getszAlarmNum0());
			System.out.println("szAlarmNum1:"+getszAlarmNum1());
			System.out.println("szAlarmNum2:"+getszAlarmNum2());
			System.out.println("szAlarmNum3:"+getszAlarmNum3());
		
			System.out.println("uOfferSize:"+getOfferSize());
			System.out.println("uImageSize:"+getImageSize());
			System.out.println("uMirror:"+getMirror());
			System.out.println("uFlip:"+getFlip());
			System.out.println("uRequestStream:"+getRequestStream());
			System.out.println("uBitrate1:"+getBitrate1());
			System.out.println("uFramerate1Val:"+getFramerate1Val());
			System.out.println("uFramerate1:"+getFramerate1());
			System.out.println("uBitrate2:"+getBitrate2());
			System.out.println("uFramerate2Val:"+getFramerate2Val());
			System.out.println("uFramerate2:"+getFramerate2());
			System.out.println("uImagesource:"+getImagesource());
			System.out.println("uChangePWD:"+getChangePWD());
			System.out.println("szNewPwd:"+getNewPwd());
			System.out.println("nDeviceNICType:"+getDeviceNICType());
			System.out.println("uEnableAudio:"+getEnableAudio());
			System.out.println("bgioinenable:"+getbgioinenable());
			System.out.println("bgiointype:"+getbgiointype());
			System.out.println("bgiooutenable:"+getbgiooutenable());
			System.out.println("bgioouttype:"+getbgioouttype());
			System.out.println("bAlarmEnable:"+getbAlarmEnable());
			System.out.println("cRs485baudrate:"+getcRs485baudrate());
			System.out.println("uSnapInterval:"+getuSnapInterval());
			System.out.println("nAudioEncoderType:"+getnAudioEncoderType());
			System.out.println("uCloudSaveType:"+getuCloudSaveType());
			System.out.println("uCloudSaveStream:"+getuCloudSaveStream());
			System.out.println("uCloudSaveTime:"+getuCloudSaveTime());
			System.out.println("nRateControl1:"+getnRateControl1());
			System.out.println("nRateControl2:"+getnRateControl2());
			System.out.println("nQPMax1:"+getnQPMax1());
			System.out.println("nQPMin1:"+getnQPMin1());
			System.out.println("nQPMax2:"+getnQPMax2());
			System.out.println("nQPMin2:"+getnQPMin2());
			System.out.println("nIPRatio1:"+getnIPRatio1());
			System.out.println("nIPRatio2:"+getnIPRatio2());
			System.out.println("bOSDTimeStampEnable1:"+getbOSDTimeStampEnable1());
			System.out.println("bOSDDateStampEnable1:"+getbOSDDateStampEnable1());
			System.out.println("bOSDTimeStampEnable2:"+getbOSDTimeStampEnable2());
			System.out.println("bOSDDateStampEnable2:"+getbOSDDateStampEnable2());
			System.out.println("bRTST_On:"+getbRTST_On());
			System.out.println("uBrightness:"+getuBrightness());
			System.out.println("uContrast:"+getuContrast());
			System.out.println("uSaturation:"+getuSaturation());
			System.out.println("nIRLightControlMethod:"+getnIRLightControlMethod());
			System.out.println("nAlarmAudioPlay:"+getnAlarmAudioPlay());
			System.out.println("nAlarmDuration:"+getnAlarmDuration());
			
			
			System.out.println("bAlarmUploadFTP:"+getbAlarmUploadFTP());
			System.out.println("bAlarmSaveToSD:"+getbAlarmSaveToSD());
			System.out.println("bSetFTPSMTP:"+getbSetFTPSMTP());
			System.out.println("servier_ip:"+getServier_ip());
			System.out.println("username:"+getUsername());
			System.out.println("password:"+getPassword());
			System.out.println("uPort:"+getuPort());
			System.out.println("szBindAccont:"+getBindAccont());
			System.out.println("szDevSAddr:"+getszDevSAddr());
			System.out.println("uDevSPort:"+getuDevSPort());
			System.out.println("szSMTPReceiver:"+getSzSMTPReceiver());
			System.out.println("motionenable:"+getMotionenable());
			System.out.println("motioncenable:"+getmotioncenable());
			System.out.println("motionlevel:"+getMotionlevel());
			System.out.println("motioncvalue:"+getmotioncvalue());
			System.out.println("motionblock:"+getmotionblock());
			System.out.println("bDeviceRest:"+getbDeviceRest());
			System.out.println("bEnableEmailRcv:"+getbEnableEmailRcv());
			System.out.println("bAttachmentType:"+getbAttachmentType());
			System.out.println("ntp_timezone:"+getNtp_timezone());
			System.out.println("nYear:"+getnYear());
			System.out.println("nMon:"+getnMon());
			System.out.println("nDay:"+getnDay());
			System.out.println("nHour:"+getnHour());
			System.out.println("nMin:"+getnMin());
			System.out.println("nSec:"+getnSec());
			System.out.println("nSdinsert:"+getnSdinsert());
			System.out.println("bSchedulesUploadFTP:"+getbSchedulesUploadFTP());
			System.out.println("bSchedulesSaveToSD:"+getbSchedulesSaveToSD());
			System.out.println("**********************device info********************");
		}

		public String getPacketFlag()
		{ 
			return byteToString(szPacketFlag);
		}

		public String getDeviceName()
		{ 		
			return byteToString(szDeviceName);
		}

		public String getDeviceType()
		{		
			return byteToString(szDeviceType);
		}

		public String getDeviceIP()
		{ 
			return byteToString(szDeviceIP);
		}

		public String getDeviceMasK()
		{ 		
			return byteToString(szDeviceMasK);
		}

		public String getDeviceGateWay()
		{		
			return byteToString(szDeviceGateWay);
		}

		public String getMultiAddr()
		{ 
			return byteToString(szMultiAddr);
		}

		public String getMacAddr_LAN()
		{ 
			return byteToString(szMacAddr_LAN);
		}
		
		public String getMacAddr_WIFI()
		{ 
			return byteToString(szMacAddr_WIFI);
		}
		
		public String getDNS0()
		{ 
			return byteToString(szDNS0);
		}

		public String getDNS1()
		{ 		
			return byteToString(szDNS1);
		}
		
		public String getszUserName()
		{ 		
			return byteToString(szUserName);
		}
		
		public String getPwd()
		{ 
			return byteToString(szPwd);
		}

		public String getCameraVer()
		{ 			
			return byteToString(szCameraVer);
		}

		public String getWanServerIP()
		{ 
			return byteToString(szWanServerIP);
		}

		public String getServerPort()
		{		
			return byteToString(szServerPort);
		}

		public String getCamSerial()
		{ 
			return byteToString(szCamSerial);
		}
		
		public String getTutkID()
		{ // tutkID
			return byteToString(szTutkID);
		}
		public int getSdTotal()
		{ // ��ʶ������
			return nSdTotal;
		}
		public int getSdFree()
		{ // ��ʶ������
			return nSdFree;
		}
		
		public byte getBattery()
		{ // tutkID
			return nBattery;
		}
		
		public String getsz3GUser()
		{ 
			return byteToString(sz3GUser);
		}

		public String getsz3GPWD()
		{ 
			return byteToString(sz3GPWD);
		}

		public String getsz3GAPN()
		{
			return byteToString(sz3GAPN);
		}

		public String getszDialNum()
		{ 		
			return byteToString(szDialNum);
		}

		public String getszAlarmNum0()
		{			
			return byteToString(szAlarmNum0);
		}

		public String getszAlarmNum1()
		{
			return byteToString(szAlarmNum1);
		}

		public String getszAlarmNum2()
		{ 	
			return byteToString(szAlarmNum2);
		}
		
		public String getszAlarmNum3()
		{ 	
			return byteToString(szAlarmNum3);
		}

		public String getNewPwd()
		{ // the new password
			return byteToString(szNewPwd);
		}
		
		public String getRevsered2()
		{ 
			return byteToString(szRevsered2);
		}
		
		public String getBindAccont()
		{ 
			return byteToString(szBindAccont);
		}
		
		public String getszDevSAddr()
		{			
			return byteToString(szDevSAddr);
		}
		
		public int getuDevSPort()
		{ 			
			return uDevSPort;
		}
		

		public void setPacketFlag(String strValue)
		{ 
			szPacketFlag = StringToByte(strValue, szPacketFlag.length);
		}

		public void setDeviceName(String strValue)
		{	
			szDeviceName = StringToByte(strValue, szDeviceName.length);
		}

		public void setDeviceType(String strValue)
		{ 		
			szDeviceType = StringToByte(strValue, szDeviceType.length);
		}

		public void setDeviceIP(String strValue)
		{ 
			szDeviceIP = StringToByte(strValue, szDeviceIP.length);
		}

		public void setDeviceMasK(String strValue)
		{ 	
			szDeviceMasK = StringToByte(strValue, szDeviceMasK.length);
		}

		public void setDeviceGateWay(String strValue)
		{	
			szDeviceGateWay = StringToByte(strValue, szDeviceGateWay.length);
		}

		public void setMultiAddr(String strValue)
		{ 
			szMultiAddr = StringToByte(strValue, szMultiAddr.length);
		}

		public void setMacAddr_LAN(String strValue)
		{ 
			szMacAddr_LAN = StringToByte(strValue, szMacAddr_LAN.length);
		}
		
		public void setMacAddr_WIFI(String strValue)
		{ 
			szMacAddr_WIFI = StringToByte(strValue, szMacAddr_WIFI.length);
		}
		
		public void setDNS0(String strValue)
		{ 
			szDNS0 = StringToByte(strValue, szDNS0.length);
		}

		public void setDNS1(String strValue)
		{ 			
			szDNS1 = StringToByte(strValue, szDNS1.length);
		}

		public void setszUserName(String strValue)
		{ 		
			szUserName = StringToByte(strValue, szUserName.length);
		}

		public void setPwd(String strValue)
		{ 
			szPwd = StringToByte(strValue, szPwd.length);
		}

		public void setCameraVer(String strValue)
		{ 
			szCameraVer = StringToByte(strValue, szCameraVer.length);
		}

		public void setWanServerIP(String strValue)
		{ 
			szWanServerIP = StringToByte(strValue, szWanServerIP.length);
		}

		public void setServerPort(String strValue)
		{		
			szServerPort = StringToByte(strValue, szServerPort.length);
		}

		public void setCamSerial(String strValue)
		{ 
			szCamSerial = StringToByte(strValue, szCamSerial.length);
		}
		
		public void setTutkID(String strValue)
		{ // tutkID
			szTutkID = StringToByte(strValue, szTutkID.length);
		}

		public void setSDTotal(int SdTotal)
		{ // tutkID
			nSdTotal = SdTotal;
		}
		
		public void setSdFree(int SdFree)
		{ // tutkID
			nSdFree = SdFree;
		}
		
		public void setBattery(byte Battery)
		{ // tutkID
			nBattery = Battery;
		}
		
		public void setsz3GUser(String strValue)
		{ 
			sz3GUser = StringToByte(strValue, sz3GUser.length);
		}

		public void setsz3GPWD(String strValue)
		{ 
			sz3GPWD = StringToByte(strValue, sz3GPWD.length);
		}

		public void setsz3GAPN(String strValue)
		{ 
			sz3GAPN = StringToByte(strValue, sz3GAPN.length);
		}

		public void setszDialNum(String strValue)
		{ 		
			szDialNum = StringToByte(strValue, szDialNum.length);
		}

		public void setszAlarmNum0(String strValue)
		{ 		
			szAlarmNum0 = StringToByte(strValue, szAlarmNum0.length);
		}

		public void setszAlarmNum1(String strValue)
		{  
			szAlarmNum1 = StringToByte(strValue, szAlarmNum1.length);
		}

		public void setszAlarmNum2(String strValue)
		{ //
			szAlarmNum2 = StringToByte(strValue, szAlarmNum2.length);
		}
		
		public void setszAlarmNum3(String strValue)
		{ //
			szAlarmNum3 = StringToByte(strValue, szAlarmNum3.length);
		}
		
		public void setNewPwd(String strValue)
		{ // the new password
			szNewPwd = StringToByte(strValue, szNewPwd.length);
		}

		public void setBindAccont(String strValue)
		{ 
			szBindAccont = StringToByte(strValue, szBindAccont.length);
		}
		
		public void setszDevSAddr(String strValue)
		{ 		
			szDevSAddr = StringToByte(strValue, szDevSAddr.length);
		}
		
		public int getCmd()
		{ 			
			return nCmd;
		}

		public int getMaxChannel()
		{		
			return nMaxChannel;
		}

		public int getMultiPort()
		{ 		
			return nMultiPort;
		}

		public int getDataPort()
		{ 
			return nDataPort;
		}

		public int getWebServerPort()
		{ 
			return nWebServerPort;
		}

		public int getEnableDeviceDHCP()
		{ 
			return nEnableDeviceDHCP;
		}

		public int getOfferSize()
		{ 		
			return uOfferSize;
		}

		public int getImageSize()
		{ 		
			return uImageSize;
		}

		public int getMirror()
		{ 
			return uMirror;
		}

		public int getFlip()
		{ 
			return uFlip;
		}

		public int getRequestStream()
		{//
			return uRequestStream;
		}

		public int getBitrate1()
		{ 			
			return uBitrate1;
		}
		
		public short getFramerate1Val()
		{ 	
			return uFramerate1Val;
		}
		

		public short getFramerate1()
		{ 
			return uFramerate1;
		}

	
		public int getBitrate2()
		{ 			
			return uBitrate2;
		}
		
		public short getFramerate2Val()
		{ 		
			return uFramerate2Val;
		}
		
		public short getFramerate2()
		{ 
			return uFramerate2;
		}

		public int getImagesource()
		{ 
			return uImagesource;
		}

		public int getChangePWD()
		{ // 1: need to change 0: not to change
			return uChangePWD;
		}

		public int getDeviceNICType()
		{ // 0 wired NIC(){1 wifi NIC
			return nDeviceNICType;
		}

		public int getEnableAudio()
		{ 
			return uEnableAudio;
		}
		
		public byte getbgioinenable()
		{  ///< GIO input enable
			return bgioinenable;
		}
		
		public byte getbgiointype()
		{  ///< GIO input type
			return bgiointype;
		}
		
		public byte getbgiooutenable()
		{   ///< GIO output enable
			return bgiooutenable;
		}
		
		public byte getbgioouttype()
		{   ///< GIO output type
			return bgioouttype;
		}
		
		public byte getbAlarmEnable()
		{   ///alarm enable or disable
			return bAlarmEnable;
		}
		
		public byte getcRs485baudrate()
		{   ///0-9600 1-4800 2-2400  3-1200, 4-disable-gpio 5-disable-rs232
			return cRs485baudrate;
		}
		
		public short getuSnapInterval()
		{   ///from 1minute to 24hour=24*60, 0--disable, 2014-01-26
			return uSnapInterval;
		}
		
		public byte getnAudioEncoderType()
		{   ///0--G711 1--aac 2--adpcm 3--pcm
			return nAudioEncoderType;
		}
		
		public byte getuCloudSaveType()
		{   ///0--none,  1--sd, 2--cloud, server 3- all
			return uCloudSaveType;
		}
		
		public byte getuCloudSaveStream()
		{   ///0--big 1--small
			return uCloudSaveStream;
		}
		
		public short getuCloudSaveTime()
		{   ///0--5, n>1--10second*n
			return uCloudSaveTime;
		}
		
		///////rate control, if you don't know how to use it, please don't touch these paras...
		public byte getnRateControl1()
		{   //0--auto 1--vbr 2--cbr
			return nRateControl1;
		}
		
		public byte getnRateControl2()
		{   //0--auto 1--vbr 2--cbr
			return nRateControl2;
		}
		
		public byte getnQPMax1()
		{  
			return nQPMax1;
		}
		
		public byte getnQPMin1()
		{  
			return nQPMin1;
		}
		
		public byte getnQPMax2()
		{  
			return nQPMax2;
		}
		
		public byte getnQPMin2()
		{  
			return nQPMin2;
		}
		
		public byte getnIPRatio1()
		{  
			return nIPRatio1;
		}
		
		public byte getnIPRatio2()
		{  
			return nIPRatio2;
		}
		
		///////rate control end
		//////OSD, 20140513
		public byte getbOSDTimeStampEnable1()
		{  
			return bOSDTimeStampEnable1;
		}
		
		public byte getbOSDDateStampEnable1()
		{  
			return bOSDDateStampEnable1;
		}

		public byte getbOSDTimeStampEnable2()
		{  
			return bOSDTimeStampEnable2;
		}
		
		public byte getbOSDDateStampEnable2()
		{  
			return bOSDDateStampEnable2;
		}
		
		//////RTSP on, 20140702
		public byte getbRTST_On()
		{  
			return bRTST_On;
		}

		//////bright contrast  and saturation
		public byte getuBrightness()
		{  
			return uBrightness;
		}
		
		public byte getuContrast()
		{  
			return uContrast;
		}
		
		public byte getuSaturation()
		{  
			return uSaturation;
		}
		
		public byte getnIRLightControlMethod()
		{  //0-auto, 1-on, 2-off
			return nIRLightControlMethod;
		}
		
		public byte getbFormatSD()
		{
			return bFormatSD;
		}
		
		public byte getnAlarmDuration()
		{  // /< alarm duration 0~5{10, 30, 60, 300, 600,
			return nAlarmDuration;
		}
		
		public void setCmd(int value)
		{ 		
			nCmd = value;
		}

		public void setMaxChannel(int value)
		{ 			
			nMaxChannel = value;
		}

		public void setMultiPort(int value)
		{ 		
			nMultiPort = value;
		}

		public void setDataPort(int value)
		{ 
			nDataPort = value;
		}

		public void setWebServerPort(int value)
		{ 
			nWebServerPort = value;
		}
		
		public void setEnableDeviceDHCP(int value)
		{
			nEnableDeviceDHCP = value;
		}

		public void setOfferSize(int value)
		{ 		
			uOfferSize = value;
		}

		public void setImageSize(int value)
		{ 		
			uImageSize = value;
		}

		public void setMirror(int value)
		{ 
			uMirror = value;
		}

		public void setFlip(int value)
		{ 
			uFlip = value;
		}

		public void setRequestStream(int value)
		{//
			uRequestStream = value;
		}

		public void setBitrate1(int value)
		{ 		
			uBitrate1 = value;
		}
		
		public void setFramerate1Val(short value)
		{ 		
			uFramerate1Val = value;
		}
		
		public void setFramerate1(short value)
		{ 
			uFramerate1 = value;
		}


		public void setBitrate2(int value)
		{ 		
			uBitrate2 = value;
		}
		
		public void setFramerate2Val(short value)
		{ 
			uFramerate2Val = value;
		}
		
		public void setFramerate2(short value)
		{ 
			uFramerate2 = value;
		}

		public void setImagesource(int value)
		{
			uImagesource = value;
		}

		public void setChangePWD(int value)
		{ // 1: need to change 0: not to change
			uChangePWD = value;
		}

		public void setDeviceNICType(int value)
		{ // 0 wired NIC() 1 wifi NIC
			nDeviceNICType = value;
		}

		public void setEnableAudio(int value)
		{ 	
			uEnableAudio = value;
		}
		
		public void setbgioinenable(byte bgioinenable)
		{ 	///< GIO input enable
			this.bgioinenable = bgioinenable;
		}
		
		public void setbgiointype(byte bgiointype)
		{ 	///< GIO input type
			this.bgiointype = bgiointype;
		}
		
		public void setbgiooutenable(byte bgiooutenable)
		{ 	///< GIO output enable
			this.bgiooutenable = bgiooutenable;
		}
		
		public void setbgioouttype(byte bgioouttype)
		{ 	///< GIO output type
			this.bgioouttype = bgioouttype;
		}
		
		public void setbAlarmEnable(byte bAlarmEnable)
		{ 	///alarm enable or disable
			this.bAlarmEnable = bAlarmEnable;
		}
		
		public void setcRs485baudrate(byte cRs485baudrate)
		{ 	///0-9600 1-4800 2-2400  3-1200, 4-disable-gpio 5-disable-rs232
			this.cRs485baudrate = cRs485baudrate;
		}
		
		public void setuSnapInterval(byte uSnapInterval)
		{ 	///from 1minute to 24hour=24*60, 0--disable, 2014-01-26
			this.uSnapInterval = uSnapInterval;
		}
		
		public void setnAudioEncoderType(byte nAudioEncoderType)
		{ 	///0--G711 1--aac 2--adpcm 3--pcm
			this.nAudioEncoderType = nAudioEncoderType;
		}
		
		public void setuCloudSaveType(byte uCloudSaveType)
		{ 	///0--none,  1--sd, 2--cloud, server 3- all
			this.uCloudSaveType = uCloudSaveType;
		}
		
		public void setuCloudSaveStream(byte uCloudSaveStream)
		{ 	///0--big 1--small
			this.uCloudSaveStream = uCloudSaveStream;
		}
		
		public void setuCloudSaveTime(short uCloudSaveTime)
		{ 	///0--5, n>1--10second*n
			this.uCloudSaveTime = uCloudSaveTime;
		}
		
		///////rate control, if you don't know how to use it, please don't touch these paras...
		public void setnRateControl1(byte nRateControl1)
		{ 	//0--auto 1--vbr 2--cbr
			this.nRateControl1 = nRateControl1;
		}
		
		public void setnRateControl2(byte nRateControl2)
		{ 	//0--auto 1--vbr 2--cbr
			this.nRateControl2 = nRateControl2;
		}
		
		public void setnQPMax1(byte nQPMax1)
		{ 	
			this.nQPMax1 = nQPMax1;
		}
		
		public void setnQPMin1(byte nQPMin1)
		{ 	
			this.nQPMin1 = nQPMin1;
		}
		
		public void setnQPMax2(byte nQPMax2)
		{ 	
			this.nQPMax2 = nQPMax2;
		}
		
		public void setnQPMin2(byte nQPMin2)
		{ 	
			this.nQPMin2 = nQPMin2;
		}
		
		public void setnIPRatio1(byte nIPRatio1)
		{ 	
			this.nIPRatio1 = nIPRatio1;
		}
		
		public void setnIPRatio2(byte nIPRatio2)
		{ 	
			this.nIPRatio2 = nIPRatio2;
		}
		
		///////rate control end
		//////OSD, 20140513
		public void setbOSDTimeStampEnable1(byte bOSDTimeStampEnable1)
		{ 	
			this.bOSDTimeStampEnable1 = bOSDTimeStampEnable1;
		}
		
		public void setbOSDDateStampEnable1(byte bOSDDateStampEnable1)
		{ 	
			this.bOSDDateStampEnable1 =  bOSDDateStampEnable1;
		}

		public void setbOSDTimeStampEnable2(byte bOSDTimeStampEnable2)
		{ 	
			this.bOSDTimeStampEnable2 =  bOSDTimeStampEnable2;
		}
		
		public void setbOSDDateStampEnable2(byte bOSDDateStampEnable2)
		{ 	
			this.bOSDDateStampEnable2 =  bOSDDateStampEnable2;
		}
		
		public void setbRTST_On(byte bRTST_On)
		{ 	//////RTSP on, 20140702
			this.bRTST_On =  bRTST_On;
		}
		
		public void setuBrightness(byte uBrightness)
		{ 	//////bright contrast  and saturation
			this.uBrightness =  uBrightness;
		}
		
		public void setuContrast(byte uContrast)
		{ 	
			this.uContrast =  uContrast;
		}
		
		public void setuSaturation(byte uSaturation)
		{ 	
			this.uSaturation =  uSaturation;
		}
		
		public void setnIRLightControlMethod(byte nIRLightControlMethod)
		{ 	////ir light//0-auto, 1-on, 2-off
			this.nIRLightControlMethod =  nIRLightControlMethod;
		}
		
		public void setbFormatSD(byte bFormatSD){
			this.bFormatSD = bFormatSD;
		}
		
		public void setnAlarmDuration(byte nAlarmDuration)
		{ 	////ir light//0-auto, 1-on, 2-off
			this.nAlarmDuration =  nAlarmDuration;
		}
		
		/**
		 * @return the ntp_timezone
		 */
		public byte getNtp_timezone()
		{
			return (byte) (ntp_timezone & 0x7f);
		}

		/**
		 * @param ntp_timezone
		 *            the ntp_timezone to set
		 */
		public void setNtp_timezone(byte ntp_timezone)
		{
			this.ntp_timezone = (byte) (ntp_timezone | 0x80);
		}

		/**
		 * @return the nYear
		 */
		public int getnYear()
		{
			return nYear;
		}

		/**
		 * @param nYear
		 *            the nYear to set
		 */
		public void setnYear(int nYear)
		{
			this.nYear = nYear;
		}

		/**
		 * @return the nMon
		 */
		public byte getnMon()
		{
			return nMon;
		}

		/**
		 * @param nMon
		 *            the nMon to set
		 */
		public void setnMon(byte nMon)
		{
			this.nMon = nMon;
		}

		/**
		 * @return the nDay
		 */
		public byte getnDay()
		{
			return nDay;
		}

		/**
		 * @param nDay
		 *            the nDay to set
		 */
		public void setnDay(byte nDay)
		{
			this.nDay = nDay;
		}

		/**
		 * @return the nHour
		 */
		public byte getnHour()
		{
			return nHour;
		}

		/**
		 * @param nHour
		 *            the nHour to set
		 */
		public void setnHour(byte nHour)
		{
			this.nHour = nHour;
		}

		/**
		 * @return the nMin
		 */
		public byte getnMin()
		{
			return nMin;
		}

		/**
		 * @param nMin
		 *            the nMin to set
		 */
		public void setnMin(byte nMin)
		{
			this.nMin = nMin;
		}

		/**
		 * @return the nSec
		 */
		public byte getnSec()
		{
			return nSec;
		}

		/**
		 * @param nSec
		 *            the nSec to set
		 */
		public void setnSec(byte nSec)
		{
			this.nSec = nSec;
		}

		/**
		 * @return the nSdinsert
		 */
		public byte getnSdinsert()
		{
			return nSdinsert;
		}

		/**
		 * @param nSdinsert
		 *            the nSdinsert to set
		 */
		public void setnSdinsert(byte nSdinsert)
		{
			this.nSdinsert = nSdinsert;
		}

		/**
		 * @return the bSchedulesUploadFTP
		 */
		public byte getbSchedulesUploadFTP()
		{
			return bSchedulesUploadFTP;
		}

		/**
		 * @param bSchedulesUploadFTP
		 *            the bSchedulesUploadFTP to set
		 */
		public void setbSchedulesUploadFTP(byte bSchedulesUploadFTP)
		{
			this.bSchedulesUploadFTP = bSchedulesUploadFTP;
		}

		/**
		 * @return the bSchedulesSaveToSD
		 */
		public byte getbSchedulesSaveToSD()
		{
			return bSchedulesSaveToSD;
		}

		/**
		 * @param bSchedulesSaveToSD
		 *            the bSchedulesSaveToSD to set
		 */
		public void setbSchedulesSaveToSD(byte bSchedulesSaveToSD)
		{
			this.bSchedulesSaveToSD = bSchedulesSaveToSD;
		}

		/**
		 * @return the bDeviceRest
		 */
		public byte getbDeviceRest()
		{
			return bDeviceRest;
		}

		/**
		 * @param bDeviceRest
		 *            the bDeviceRest to set
		 */
		public void setbDeviceRest(byte bDeviceRest)
		{
			this.bDeviceRest = bDeviceRest;
		}

		/**
		 * @return the motionenable
		 */
		public byte getMotionenable()
		{
			return motionenable;
		}
		
		public byte getmotioncenable()
		{
			return motioncenable;
		}
		
		/**
		 * @param motionenable
		 *            the motionenable to set
		 */
		public void setMotionenable(byte motionenable)
		{
			this.motionenable = motionenable;
		}
		
		public void setmotioncenable(byte motioncenable)
		{
			this.motioncenable = motioncenable;
		}

		/**
		 * @return the nAlarmAudioPlay
		 */
		public byte getnAlarmAudioPlay()
		{
			return nAlarmAudioPlay;
		}

		/**
		 * @param nAlarmAudioPlay
		 *            the nAlarmAudioPlay to set
		 */
		public void setnAlarmAudioPlay(byte nAlarmAudioPlay)
		{
			this.nAlarmAudioPlay = nAlarmAudioPlay;
		}

		/**
		 * @return the bEnableEmailRcv
		 */
		public byte getbEnableEmailRcv()
		{
			return bEnableEmailRcv;
		}

		/**
		 * @param bEnableEmailRcv
		 *            the bEnableEmailRcv to set
		 */
		public void setbEnableEmailRcv(byte bEnableEmailRcv)
		{
			this.bEnableEmailRcv = bEnableEmailRcv;
		}

		/**
		 * @return the bAttachmentType
		 */
		public byte getbAttachmentType()
		{
			return bAttachmentType;
		}

		/**
		 * @param bAttachmentType
		 *            the bAttachmentType to set
		 */
		public void setbAttachmentType(byte bAttachmentType)
		{
			this.bAttachmentType = bAttachmentType;
		}

		/**
		 * @return the szSMTPReceiver
		 */
		public byte[] getSzSMTPReceiver()
		{
			return szSMTPReceiver;
		}

		/**
		 * @param szSMTPReceiver
		 *            the szSMTPReceiver to set
		 */
		public void setSzSMTPReceiver(byte szSMTPReceiver[])
		{
			this.szSMTPReceiver = szSMTPReceiver;
		}

		/**
		 * @return the bAlarmUploadFTP
		 */
		public byte getbAlarmUploadFTP()
		{
			return bAlarmUploadFTP;
		}

		/**
		 * @param bAlarmUploadFTP
		 *            the bAlarmUploadFTP to set
		 */
		public void setbAlarmUploadFTP(byte bAlarmUploadFTP)
		{
			this.bAlarmUploadFTP = bAlarmUploadFTP;
		}

		/**
		 * @return the bAlarmSaveToSD
		 */
		public byte getbAlarmSaveToSD()
		{
			return bAlarmSaveToSD;
		}

		/**
		 * @param bAlarmSaveToSD
		 *            the bAlarmSaveToSD to set
		 */
		public void setbAlarmSaveToSD(byte bAlarmSaveToSD)
		{
			this.bAlarmSaveToSD = bAlarmSaveToSD;
		}

		/**
		 * @return the bSetFTPSMTP
		 */
		public byte getbSetFTPSMTP()
		{
			return bSetFTPSMTP;
		}

		/**
		 * @param bSetFTPSMTP
		 *            the bSetFTPSMTP to set
		 */
		public void setbSetFTPSMTP(byte bSetFTPSMTP)
		{
			this.bSetFTPSMTP = bSetFTPSMTP;
		}

		/**
		 * @return the servier_ip
		 */
		public byte[] getServier_ip()
		{
			return servier_ip;
		}

		/**
		 * @param servier_ip
		 *            the servier_ip to set
		 */
		public void setServier_ip(byte servier_ip[])
		{
			this.servier_ip = servier_ip;
		}

		/**
		 * @return the username
		 */
		public byte[] getUsername()
		{
			return username;
		}

		/**
		 * @param username
		 *            the username to set
		 */
		public void setUsername(byte username[])
		{
			this.username = username;
		}

		/**
		 * @return the password
		 */
		public byte[] getPassword()
		{
			return password;
		}

		/**
		 * @param password
		 *            the password to set
		 */
		public void setPassword(byte password[])
		{
			this.password = password;
		}

		/**
		 * @return the uPort
		 */
		public int getuPort()
		{
			return uPort;
		}

		/**
		 * @param uPort
		 *            the uPort to set
		 */
		public void setuPort(int uPort)
		{
			this.uPort = uPort;
		}
		
		public void setuDevSPort(int uDevSPort)
		{
			this.uDevSPort = uDevSPort;
		}
		
		
		/**
		 * @return the motionlevel
		 */
		public byte getMotionlevel()
		{
			return motionlevel;
		}

		/**
		 * @param motionlevel
		 *            the motionlevel to set
		 */
		public void setMotionlevel(byte motionlevel)
		{
			this.motionlevel = motionlevel;
		}
		
		public byte getmotioncvalue()
		{
			return motioncvalue;
		}
		
		public String getmotionblock()
		{
			return byteToString(motionblock);
		}
		
		public void setmotionblock(byte motionblock[])
		{
			this.motionblock = motionblock;
		}
		
		public void setmotioncvalue(byte motioncvalue)
		{
			this.motioncvalue = motioncvalue;
		}
		
		
		@Override
		public boolean equals(Object obj)
		{
			if (!(obj instanceof Device3GInfo))
			{
				return false;
			}
			Device3GInfo o = (Device3GInfo) obj;
			if (this == o)
			{
				return true;
			}

			this.setCmd(nCmd);	
			o.setCmd(nCmd); 

			ByteBuffer buffer = o.toByteBuffer();
			if (buffer.equals(this.toByteBuffer()))
			{
				return true;
			}
			return false;
		}

		@Override
		public Object clone()
		{
			Device3GInfo o = null;
			try
			{
				o = (Device3GInfo) super.clone();
				o.szPacketFlag = (byte[]) szPacketFlag.clone();
				o.szDeviceName = (byte[]) szDeviceName.clone();
				o.szDeviceType = (byte[]) szDeviceType.clone();
				o.szDeviceIP = (byte[]) szDeviceIP.clone();
				o.szDeviceMasK = (byte[]) szDeviceMasK.clone();
				o.szDeviceGateWay = (byte[]) szDeviceGateWay.clone();
				o.szMultiAddr = (byte[]) szMultiAddr.clone();
				o.szMacAddr_LAN = (byte[]) szMacAddr_LAN.clone();
				o.szMacAddr_WIFI =(byte[]) szMacAddr_WIFI.clone();
				o.szRevsered0 = (byte[]) szRevsered0.clone();
				o.szDNS0 = (byte[]) szDNS0.clone();
				o.szDNS1 = (byte[]) szDNS1.clone();
				o.szUserName = (byte[]) szUserName.clone();
				o.szPwd = (byte[]) szPwd.clone();
				o.szCameraVer = (byte[]) szCameraVer.clone();
				o.szWanServerIP = (byte[]) szWanServerIP.clone();
				o.szServerPort = (byte[]) szServerPort.clone();
				o.szCamSerial = (byte[]) szCamSerial.clone();
				o.szTutkID = (byte[]) szTutkID.clone();
				o.szRevsered1 = (byte[]) szRevsered1.clone();
				
				o.sz3GUser = (byte[]) sz3GUser.clone();
				o.sz3GPWD = (byte[]) sz3GPWD.clone();
				o.sz3GAPN = (byte[]) sz3GAPN.clone();
				o.szDialNum = (byte[]) szDialNum.clone();
				o.szAlarmNum0 = (byte[]) szAlarmNum0.clone();
				o.szAlarmNum1 = (byte[]) szAlarmNum1.clone();
				o.szAlarmNum2 = (byte[]) szAlarmNum2.clone();
				o.szAlarmNum3 = (byte[]) szAlarmNum3.clone();
				o.szNewPwd = (byte[]) szNewPwd.clone();
				o.szRevsered2 = (byte[]) szRevsered2.clone();
				o.setServier_ip((byte[]) getServier_ip().clone());
				o.setUsername((byte[]) getUsername().clone());
				o.setPassword((byte[]) getPassword().clone());
				o.szBindAccont = (byte[]) szBindAccont.clone();
				o.szDevSAddr = (byte[]) szDevSAddr.clone();
				o.setSzSMTPReceiver((byte[]) getSzSMTPReceiver().clone());
				o.motionblock = (byte[]) motionblock.clone();
				o.aSchedules = (Schedule[]) aSchedules.clone();

			} catch (CloneNotSupportedException e)
			{
				e.printStackTrace();
			}
			return o;
		}
}
