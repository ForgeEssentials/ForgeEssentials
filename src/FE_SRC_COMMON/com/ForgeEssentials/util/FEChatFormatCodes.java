package com.ForgeEssentials.util;

public enum FEChatFormatCodes
{

	BLACK
	{
		/* Perhaps add somthing like this and then parse all the regexes in chat so all you have to do is add them here?
		*public String getRegex()
		*{
		*	return "%Black";
		*}
		*/
		@Override
		public String toString()
		{
			return "\u00a70";
		}
	},
	DARKBLUE
	{
		@Override
		public String toString()
		{
			return "\u00a71";
		}
	},
	DARKGREEN
	{
		@Override
		public String toString()
		{
			return "\u00a72";
		}
	},
	DARKAQUA
	{
		@Override
		public String toString()
		{
			return "\u00a73";
		}
	},
	DARKRED
	{
		@Override
		public String toString()
		{
			return "\u00a74";
		}
	},
	PURPLE
	{
		@Override
		public String toString()
		{
			return "\u00a75";
		}
	},
	GOLD
	{
		@Override
		public String toString()
		{
			return "\u00a76";
		}
	},
	GREY
	{
		@Override
		public String toString()
		{
			return "\u00a77";
		}
	},
	DARKGREY
	{
		@Override
		public String toString()
		{
			return "\u00a78";
		}
	},
	INDIGO
	{
		@Override
		public String toString()
		{
			return "\u00a79";
		}
	},
	GREEN
	{
		@Override
		public String toString()
		{
			return "\u00a7a";
		}
	},
	AQUA
	{
		@Override
		public String toString()
		{
			return "\u00a7b";
		}
	},
	RED
	{
		@Override
		public String toString()
		{
			return "\u00a7c";
		}
	},
	PINK
	{
		@Override
		public String toString()
		{
			return "\u00a7d";
		}
	},
	YELLOW
	{
		@Override
		public String toString()
		{
			return "\u00a7e";
		}
	},

	WHITE
	{
		@Override
		public String toString()
		{
			return "\u00a7f";
		}
	},
	RANDOM
	{
		@Override
		public String toString()
		{
			return "\u00a7k";
		}
	},
	BOLD
	{
		@Override
		public String toString()
		{
			return "\u00a7l";
		}
	},
	CODE
	{
		@Override
		public String toString()
		{
			return "\u00a7";
		}
	},
	STRIKE
	{
		@Override
		public String toString()
		{
			return "\u00a7m";
		}
	},
	UNDERLINE
	{
		@Override
		public String toString()
		{
			return "\u00a7n";
		}
	},
	ITALICS
	{
		@Override
		public String toString()
		{
			return "\u00a7o";
		}
	},
	RESET
	{
		@Override
		public String toString()
		{
			return "\u00a7r";
		}
	};
	SMILE
	{
		@Override
		public String toString()
		{
			return "\u263A";
		}
	};
	COPYRIGHTED
	{
		@Override
		public String toString()
		{
			return "\u00A9";
		}
	};
	REGISTERED
	{
		@Override
		public String toString()
		{
			return "\u00AE";
		}
	};
	DIMOND
	{
		@Override
		public String toString()
		{
			return "\u2662";
		}
	};
	SPADE
	{
		@Override
		public String toString()
		{
			return "\u2664";
		}
	};
	CLUB
	{
		@Override
		public String toString()
		{
			return "\u2667";
		}
	};
	HEART
	{
		@Override
		public String toString()
		{
			return "\u2661";
		}
	};
	FEMALE
	{
		@Override
		public String toString()
		{
			return "\u2640";
		}
	};
	MALE
	{
		@Override
		public String toString()
		{
			return "\u2642";
		}
	};

}
