# connect-ktb-edc-his

เชื่อมต่อจาก HIS ไปยังเครื่อง EDC ของ KTB เพื่อส่งข้อมูลเบิกจ่ายตรง และปิดสิทธิ ตามเอกสาร POS INTERFACE
MESSAGE SPECIFICATIONS version 2.13

สามารถรองรับบัตรประเภทต่างๆ ดังรายการต่อไปนี้

- บัตรประชาชน(Citizen Card)

ประเภทรายการที่สามารถรองรับให้ใช้งาน

- ใช้สิทธิ์รักษาพยาบาล
- รายการยกเลิก
- รายการพิมพ์สลิปซ้ำ
- รายการโอนยอด

ตารางแสดงข้อมูล Transaction Code

Code No. | Details
---------|---------
 11 | ผู้ป่วยนอกทั่วไป สิทธิตนเองและครอบครัว
 12 | ผู้ป่วยนอกทั่วไป สิทธิบุตร 0-7 ปี
 13 | ผู้ป่วยนอกทั่วไป สิทธิคู่สมรสต่างชาติ
14 | ผู้ป่วยนอกทั่วไป ไม่สามารถใช้บัตรได้
21  | หน่วยไตเทียม สิทธิตนเองและครอบครัว
22  | หน่วยไตเทียม สิทธิบุตร 0-7 ปี
23  | หน่วยไตเทียม สิทธิคู่สมรสต่างชาติ
24  | หน่วยไตเทียม ไม่สามารถใช้บัตรได้
31  | หน่วยรังสีผู้ เป็นมะเร็ง สิทธิตนเองและครอบครัว
32  | หน่วยรังสีผู้ เป็นมะเร็งสิทธิบุตร 0-7 ปี
33  | หน่วยรังสีผู้ เป็นมะเร็งสิทธิคู่สมรสต่างชาติ
34  | หน่วยรังสีผู้ เป็นมะเร็งไม่สามารถใช้ บัตรได้
60  | ใช้สิทธิบัตรทอง
26  | รายการยกเลิก
50  | รายการโอนยอด
92  | รายการพิมพ์สลิปซ้ำ
