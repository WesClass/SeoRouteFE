package com.example.seoroute.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seoroute.R
import com.example.seoroute.model.EventTagStyle
import com.example.seoroute.model.SeoEvent
import com.example.seoroute.ui.theme.SeoBorder
import com.example.seoroute.ui.theme.SeoFilterChipBg
import com.example.seoroute.ui.theme.SeoMapBg
import com.example.seoroute.ui.theme.SeoMapBlock
import com.example.seoroute.ui.theme.SeoMapCaptionBg
import com.example.seoroute.ui.theme.SeoPrimary
import com.example.seoroute.ui.theme.SeoPrimaryLight
import com.example.seoroute.ui.theme.SeoSurface
import com.example.seoroute.ui.theme.SeoTextCaption
import com.example.seoroute.ui.theme.SeoTextMuted
import com.example.seoroute.ui.theme.SeoTextPrimary
import com.example.seoroute.ui.theme.SeoTextSecondary

/**
 * Reusable search bar with optional trailing filter icon button.
 */
@Composable
fun SeoSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    onSearchBarClick: (() -> Unit)? = null,
    showFilterButton: Boolean = false,
    onFilterClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .then(
                if (onSearchBarClick != null) {
                    Modifier.clickable { onSearchBarClick() }
                } else Modifier
            ),
        shape = RoundedCornerShape(14.dp),
        color = SeoSurface,
        border = BorderStroke(1.dp, SeoBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "Search",
                tint = Color.Unspecified,
                modifier = Modifier.size(19.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (query.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = SeoTextMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
                if (!readOnly && onSearchBarClick == null) {
                    BasicTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        singleLine = true,
                        textStyle = TextStyle(
                            color = SeoTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        cursorBrush = SolidColor(SeoPrimary),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else if (query.isNotEmpty()) {
                    Text(
                        text = query,
                        color = SeoTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
            if (showFilterButton) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onFilterClick?.invoke() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_filter_sliders),
                        contentDescription = "Filter events",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Reusable horizontal event card used in Discover ("Happening soon") and Explore Events list.
 */
@Composable
fun EventHorizontalCard(
    event: SeoEvent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeOverride: EventTagStyle = event.listBadge
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(96.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = SeoSurface,
        border = BorderStroke(1.dp, SeoBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = event.imageRes),
                contentDescription = event.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(width = 78.dp, height = 76.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(vertical = 12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = event.title,
                        color = SeoTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 17.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.shortDateLocation,
                        color = SeoTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 13.sp
                    )
                }
                EventBadgePill(
                    tag = badgeOverride,
                    minWidth = if (badgeOverride.text == "Booking") 75.dp else 51.dp,
                    height = 23.dp
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .padding(end = 6.dp)
                    .size(14.dp)
            )
        }
    }
}

/**
 * Reusable pill badge for event tags (e.g. Free, Booking, Music, Outdoor, Free open, Indoor).
 */
@Composable
fun EventBadgePill(
    tag: EventTagStyle,
    modifier: Modifier = Modifier,
    minWidth: Dp = 51.dp,
    height: Dp = 24.dp,
    horizontalPadding: Dp = 12.dp
) {
    Box(
        modifier = modifier
            .height(height)
            .widthIn(min = minWidth)
            .clip(RoundedCornerShape(50))
            .background(tag.backgroundColor)
            .padding(horizontal = horizontalPadding),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = tag.text,
            color = tag.textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 13.sp
        )
    }
}

/**
 * Reusable selectable filter pill used in Explore screen filter bar and Event Filter modal.
 */
@Composable
fun SelectableFilterPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    unselectedBackgroundColor: Color = SeoSurface,
    unselectedBorderColor: Color? = null,
    height: Dp = 34.dp,
    horizontalPadding: Dp = 16.dp
) {
    val bgColor = if (selected) SeoPrimary else unselectedBackgroundColor
    val textColor = if (selected) SeoSurface else SeoTextSecondary
    val shape = RoundedCornerShape(50)

    Box(
        modifier = modifier
            .height(height)
            .clip(shape)
            .background(bgColor)
            .then(
                if (!selected && unselectedBorderColor != null) {
                    Modifier.border(1.dp, unselectedBorderColor, shape)
                } else Modifier
            )
            .clickable { onClick() }
            .padding(horizontal = horizontalPadding),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Illustrative location map card matching Figma node 150:56 and 150:134.
 */
@Composable
fun IllustrativeLocationMap(
    venueName: String,
    districtCaption: String,
    highlightBlockColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(128.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(SeoMapBg)
    ) {
        // City blocks - Top row
        Box(
            modifier = Modifier
                .offset(x = 12.dp, y = 10.dp)
                .size(width = 66.dp, height = 31.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(SeoMapBlock)
        )
        Box(
            modifier = Modifier
                .offset(x = 94.dp, y = 8.dp)
                .size(width = 49.dp, height = 33.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(SeoMapBlock)
        )
        Box(
            modifier = Modifier
                .offset(x = 160.dp, y = 9.dp)
                .size(width = 70.dp, height = 30.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(SeoMapBlock)
        )
        Box(
            modifier = Modifier
                .offset(x = 246.dp, y = 9.dp)
                .size(width = 110.dp, height = 31.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(SeoMapBlock)
        )

        // City blocks - Bottom row
        Box(
            modifier = Modifier
                .offset(x = 12.dp, y = 58.dp)
                .size(width = 65.dp, height = 45.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(SeoMapBlock)
        )
        Box(
            modifier = Modifier
                .offset(x = 94.dp, y = 60.dp)
                .size(width = 49.dp, height = 45.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(SeoMapBlock)
        )
        // Highlighted Venue / Plaza block
        Box(
            modifier = Modifier
                .offset(x = 160.dp, y = 57.dp)
                .size(width = 70.dp, height = 48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(highlightBlockColor)
        )
        Box(
            modifier = Modifier
                .offset(x = 246.dp, y = 58.dp)
                .size(width = 110.dp, height = 47.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(SeoMapBlock)
        )

        // Roads
        Box(
            modifier = Modifier
                .offset(x = 0.dp, y = 44.dp)
                .fillMaxWidth()
                .height(10.dp)
                .background(SeoSurface)
        )
        Box(
            modifier = Modifier
                .offset(x = 81.dp, y = 0.dp)
                .width(9.dp)
                .fillMaxHeight()
                .background(SeoSurface)
        )
        Box(
            modifier = Modifier
                .offset(x = 234.dp, y = 0.dp)
                .width(9.dp)
                .fillMaxHeight()
                .background(SeoSurface)
        )

        // Venue Pin inside highlighted block
        Image(
            painter = painterResource(id = R.drawable.ic_detail_location),
            contentDescription = venueName,
            modifier = Modifier
                .offset(x = 185.dp, y = 61.dp)
                .size(20.dp)
        )

        // Venue floating label pill
        Box(
            modifier = Modifier
                .offset(x = 102.dp, y = 10.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(SeoSurface)
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = venueName,
                color = SeoTextPrimary,
                fontSize = if (venueName.length > 14) 11.sp else 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Bottom caption strip
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(22.dp)
                .background(SeoMapCaptionBg)
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = districtCaption,
                color = SeoTextCaption,
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

/**
 * Reusable Organizer Summary card matching Figma node 150:75 and 150:153.
 */
@Composable
fun OrganizerSummaryCard(
    monogram: String,
    name: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(SeoMapCaptionBg)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SeoPrimaryLight),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = monogram,
                color = SeoPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Organized by",
                color = SeoTextCaption,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 14.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = name,
                color = SeoTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = SeoTextCaption,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 14.sp
            )
        }
    }
}
