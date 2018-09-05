package com.klfront.utils;

import java.io.UnsupportedEncodingException;

/**
 * Created by L on 2015/11/24.
 * <p>
 * GB2312标准共收录6763个汉字，其中一级汉字3755个，二级汉字3008个。
 * <p>
 * GB 2312中对所收汉字进行了“分区”处理，每区含有94个汉字/符号。这种表示方式也称为区位码。
 * 1）01-09区为特殊符号。
 * 2）16-55区为一级汉字，按拼音排序。
 * 3）56-87区为二级汉字，按部首/笔画排序。
 * 4）10-15区及88-94区则未有编码。
 * <p>
 * 对于二级汉字，其拼音首字母不再是顺序分布，它需要通过一个偏移量来进行计算
 * 偏移量 = (区码 - 56) * 94 + 位码 - 1;
 */
public class PYCodeUtils
{
    // 存放国标一级汉字不同读音的起始区位码
    static final int[] li_SecPosValue = {
            1601, 1637, 1833, 2078, 2274, 2302, 2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858, 4027, 4086,
            4390, 4558, 4684, 4925, 5249, 5590
    };

    // 存放国标一级汉字不同读音的起始区位码对应读音
    private final static String[] lc_FirstLetter = {
            "a", "b", "c", "d", "e", "f", "g", "h", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "w", "x",
            "y", "z"
    };
    // 存放所有国标二级汉字读音
    static String ls_SecondSecTable = "cjwgnspgcgne[z[btzzzdxzkzgt[jnnjqmbsgzsczjszz" + "[pgkbzgz[zwjkgkljzwkpjqhz[w[dzlsgmrzpzwwcckznkzzgttnjjnzkkzztcjnmczlqlzpzqfqrpzslwbtgkjfzxjwzltbncxjjjjtxdttsqzzcdxxhgck" + "[phffss[zbgxlppbzll[hlxs[zm[jhsojnghdzqzklgjhsgqzhxqgkezzwzscscjxzezxadzpmdssmzjzqjzzc[j" + "[wqjbzzpxgznzcpwhkxhqkmwfbpbzdtjzzkqhzlzgxfptzjzzzpszlfchmqshgmxxsxj[" + "[dcsbbqbefsjzhxwgzkpzlqbgldlcctnmazddkssngzcsgxlzzazbnptsdkdzlhgzmzlcxpz" + "[jndqjwxqxfzzfjlejpzrxccqwqqsbnkzmgplbmjrqcflnzmzqmsqzrbcjthztqfrxqhxmjjcjlxqgjmshzkbswzemzltxfszdswlzcjqxsjnqbsctzhbftdczzdjwz" + "ghqfrxwckqkxebptlpxjzsrmebwhjlbjslzzsmdxlclqkxlhxjrzjmfqhxhwzwsbhtrxxglhqhfnm[zkldzxzpzlgg[mtcfpajjzzljtzanjgbjplqgdzzqz" + "axbkzsecjsznslzzhsxlzcghpxzhznztdsbcjkdlzazfmzdlebbgqzzkxgldndnzskjshdlzxbcghxzpkdjmmzngmmclgwzszxzjfznmlzzthcszdbdllscdd" + "nlkjzkjszcjlkwhqasdknhcsganhdaashtcplcpqzbsdmpjlpzjoqlcdhjjzsprchn[nnlhlzzqzhwzptczgwwmzffjqqqqzxaclbhkdjxdgmmzdjxzllszgx" + "gkjrzwzwzclzmssjzldbzd[fcxzhlxchzzjq[[qagmnzxpfrkssbjlzxzszglnscmhzwwmnzjjlxxhchsz[[ttxrzczxbzhcsmxjsznpwgpxxtazbgajcxlz" + "[dccwzocwkccsbnhcpdzznfczztzckxkzbsqkkztqqxfcwchczkelzqbsqzjqcclmthszwhmktlkjlzcxwheqqhtqh[pq" + "[qscfzmndmgbwhwlgsllzsdlmlxpthmjhwljzzhzjxhtxjlhxrswlwzjcbxmhzqxsdzpmgfcsglsxzmjshxpjxwmzqksmzplrthbxftpmhzxlchlhlzz" + "lxgsssstclsldclrpbhzhxzzfhb[gdmzcnqqwlqhjj[zwjzzejjdhpblqxtqkwhlchqxagtlxljxmsl[htzkzjecxjcjnmfbz[sfzwzbjzgnzsdzsqzrslj" + "pclpwxsdwejbjcbcnaztwgmpapclzqpclzxsbnmsggfnzjjbzsfzzndxhplqkzczwalsbccjx[zzgwkzpsgxfzfcdkhjgxdlqfsgdslqwzkxtmhsbgzmjzrglzj" + "bpmlmsxlzjqqhzzjczzdjwbmzklddpmjegxzhzlxhlqzqhkzcwcjmzzxnatjhzccxzpcqlbzwwztwbqcmlpmzrjcccxfpznzzljplxxzztzlgdldcklzrzzgqtg" + "jhhgjljaxfgfjzslcfdqzlclgjdjcsnzlljpjqdcclcjxmzzftsxgcgsbrzxjqqctzhgzqtjqqlzxjzlzlbczamcstzlpdjbzregklzzzhlzszqlznwczcllwjq" + "jjjkdgjzolbbzppglghtgzxzghzmzcnqszczhbhgxkamtxzxnbskzzzgjzlqjdfcjxdzgjqjjpmgwgjjjpkqsbgbmmcjssclpqpdxcdzzkz[cjddzzgzwrhjrtgz" + "nzqldkljszzgzqzjgdzkshpzmtlcpwnjafzzdjcnmwesczglbtzcgmssllzxqsxsbsjsbbsgghfjlzpmzjnlzzwdqshzxtzzwhmzzhzwdbxbtlmszzzfsxjc[dxx" + "lhjhf[sxzqhfzmzcztqcxzxrttdjhnnzzqqmnqdmmg[zdxmjgdhcdzzbffallztdltfxmxqzdngwqdbdczjdxbzgsqqddjcmbkzffxmkdmdszzszcmljdsznsbrs" + "kmkmpcklgdbqtfzswtfgglzplljzhgj[gzpzltcsmcnbtjbqfkthbzzgkpbbzmtdssxtbnpdklezcjnzddzkzddhqhsdzsctarlltkzlgecllkjlqjaqnbdkkghp" + "jtzqksecshalqfmmgjnlzjbbtmlzzxdcjpldlpcqdhzzcbzsczbzmsljflkrzjsnfrgjhxpdhzjzbzgdlqcsezgxlblgzxtwmabchecmwzjzzlljjzhlg[djlslz" + "gkdzpzxjzzzlwcxszfgwzzdlzhcljscmbjhblzzlzcblzdpdqzsxqzbztdkzxjz[cnrjmpdjgklcljbctbjddbblblczqrppxjcjlzcshltoljnmdddlngkaqhqh" + "jgzkheznmshrp[qqjchgmfprxhjgdzchghlzrzqlczqjnzsqtkqjzmszswlcfqqqxzfggzptqwlmcrnfkkfszzlqbmqammmzxctpshcptxxzzsmphpshmclmldqf" + "zqxszzzdzjzzhqpdszglstjbckbxzqzjsgpsxqzqzrqtbdkzxzkhhgflbcsmdldgdzdblzzzcxnncszbzbfglzzxswmsccmqnjqsbdqsjtxxmbltxzclzshzcxrq" + "jgjzlxzfjphzmzqqzdfqjjlzznzjcdgzzgctxmzzsctlkphtxhtlbjxjlxscdqxcbbtjfqzfsltjbtkqbxxjjljchczdbzjdczjdcprnpqcjpfczlclzxzdmxmph" + "jsgzgszzqlzlwtjpfszasmcjbtzkzcwmztcsjjljcqlwzmalbxzfbpnlsfhtgjwejjxxglljstgshjqlzfkcgnnnszfdeqfhbsaqtgzlbxmmzgszldzdqmjjrgbj" + "tkgdhgkblqkbdmbzlxwcxzttzbkmrtjzxqjbhlmhmjjzmqasldczxzqdlqcafzwzxqhz";


    /**
     * 取得给定汉字串的首字母串
     *
     * @param str 给定汉字串
     * @return 首字母串
     */
    public static String getSpells(String str)
    {
        if (str == null || str.trim().length() == 0)
        {
            return "";
        }

        String _str = "";
        for (int i = 0; i < str.length(); i++)
        {
            _str = _str + getFirstLetter(str.substring(i, i + 1));
        }

        return _str;
    }

    /**
     * 取得给定汉字的拼音首字母
     *
     * @param chinese 给定的汉字
     * @return 给定汉字的拼音首字母
     */
    public static String getFirstLetter(String chinese)
    {
        if (chinese == null || chinese.trim().length() == 0)
        {
            return "";
        }
        // 二级字库偏移量  
        int ioffset = 0;

        chinese = conversionStr(chinese, "GB2312", "ISO8859-1");

        if (chinese.length() > 1) // 判断是不是汉字  
        {
            int li_SectorCode = (int) chinese.charAt(0); // 汉字区码  
            int li_PositionCode = (int) chinese.charAt(1); // 汉字位码  
            li_SectorCode = li_SectorCode - 160;
            li_PositionCode = li_PositionCode - 160;
            int li_SecPosCode = li_SectorCode * 100 + li_PositionCode; // 汉字区位码  

            // 汉字编码范围在1601-1694,1701-1794,....一直到8701-8794范围以内  
            if (li_SecPosCode > 1600 && li_SecPosCode < 5590 && li_SecPosCode % 100 < 95)
            {
                for (int i = 0; i < 23; i++)
                {
                    if (li_SecPosCode >= li_SecPosValue[i] && li_SecPosCode < li_SecPosValue[i + 1])
                    {
                        chinese = lc_FirstLetter[i];
                        break;
                    }
                }
            }
            else if (li_SecPosCode > 5590 && li_SecPosCode < 8795 && li_SecPosCode % 100 < 95)
            {
                ioffset = (li_SectorCode - 56) * 94 + li_PositionCode - 1;
                if (ioffset >= 0 && ioffset <= 3007)
                {
                    chinese = ls_SecondSecTable.substring(ioffset, ioffset + 1);
                }
            }
            else
            {
                chinese = chinese.substring(0, 1);
            }
        }

        return chinese;
    }

    /**
     * 字符串编码转换
     *
     * @param str           要转换编码的字符串
     * @param charsetName   原来的编码
     * @param toCharsetName 转换后的编码
     * @return 经过编码转换后的字符串
     */
    private static String conversionStr(String str, String charsetName, String toCharsetName)
    {
        try
        {
            str = new String(str.getBytes(charsetName), toCharsetName);
        }
        catch (UnsupportedEncodingException ex)
        {
            ex.printStackTrace();
        }

        return str;
    }
}
