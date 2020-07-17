package ru.skillbranch.gameofthrones.ui.houses.house

import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import kotlinx.android.synthetic.main.item_character.view.*
import ru.skillbranch.gameofthrones.data.local.entities.HouseType

class CharactersAdapter(private val listener: (CharacterItem) -> Unit) :
    RecyclerView.Adapter<CharactersAdapter.CharactersVH>() {

    var items: List<CharacterItem> = listOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CharactersVH {
       val containerView = from(parent.context).inflate(
           R.layout.item_character,
           parent,
           false
       )
        return CharactersVH(containerView)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: CharactersVH, position: Int) =
        holder.bind(items[position], listener)

    fun updateItems(characters: List<CharacterItem>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean =
                items[oldPos].id  == characters[newPos].id

            override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean =
                items[oldPos]  == characters[newPos]

            override fun getOldListSize(): Int = items.size

            override fun getNewListSize(): Int = characters.size

        }

        val diffResult = DiffUtil.calculateDiff(diffCallback, true)
        items = characters
        diffResult.dispatchUpdatesTo(this)
    }


    class CharactersVH(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        fun bind(
                item: CharacterItem,
                listener: (CharacterItem) -> Unit
        ) {
            item.name.also {
                itemView.tv_name.text = if (it.isBlank()) "Unknown information" else it
            }

            item.titles
                .plus(item.aliases)
                .filter { it.isNotBlank() }
                .also {
                    containerView.tv_aliases.text =
                        if (it.isEmpty()) "Unknown information"
                        else it.joinToString(" * ")
                }

            val icon = HouseType.fromString(item.house).icon
            containerView.iv_avatar.setImageResource(icon)

            itemView.setOnClickListener { listener(item) }

        }
    }
}
